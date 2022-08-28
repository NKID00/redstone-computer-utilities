from traceback import format_exc
from typing import Any, Awaitable, Callable, Optional, TypeVar, cast
import asyncio

from .io import MethodNotFoundError, _JsonRpcIO
from .event import _Event, _Events
from .interface import Interface
from .util import (_bytes_to_base64, _base64_to_bytes,
                   _base64_to_int, _int_to_base64)
from .cli import _error

_C = TypeVar('_C', bound=Callable[..., Awaitable[Any]])


class Script:
    '''Script.  It is recommended to use ``create_script`` rather than register
    manually.'''

    def __init__(self, name: str, description: str = '',
                 permission_level: int = 2) -> None:
        '''Create a new script. It is recommended to use ``create_script``
        rather than register manually.

        :param name: Name of the script, MUST be unique among all registered
        scripts and MUST be a string consists of only letters, numbers
        and underlines.
        :param description: Description of the script, SHOULD be any unicode
        string that can be displayed as plain text in Minecraft.
        :param permission_level: Permission level required to run the script,
        SHOULD be a integer in [2, 4].'''
        self._name: str = name
        self._description: str = description
        self._permission_level: int = permission_level
        self._event_callbacks: dict[
            _Event, list[Callable[..., Awaitable]]] = {}
        self._event_method_names: dict[str, _Event] = {}
        self._io: Optional[_JsonRpcIO] = None
        self._id_lock: asyncio.Lock = asyncio.Lock()
        self._id_value: int = 0
        self._auth_key: str = ''

    @property
    def name(self) -> str:
        return self._name

    def _set_io(self, io: _JsonRpcIO):
        '''Set the underlying io.'''
        self._io = io

    @property
    def _id(self) -> str:
        self._id_value += 1
        return f'c_{self._name}_{self._id_value}'

    async def _call_api(self, method: str, **params: Any) -> Any:
        if params is None:
            params = {}
        if self._auth_key != '':
            params['authKey'] = self._auth_key
        return await cast(_JsonRpcIO, self._io).send(method, params, self._id)

    def _register_event_callback(self, event: _Event, callback: _C) -> _C:
        if event not in self._event_callbacks:
            self._event_callbacks[event] = []
        self._event_callbacks[event].append(callback)
        self._event_method_names[event.to_method_name(self)] = event
        return callback

    def _event_callback_registerer(self, event: _Event) -> Callable[[_C], _C]:
        return lambda callback: self._register_event_callback(event, callback)

    async def _dispatch_request(self, method: str, params: dict[str, Any]
                                ) -> Any:
        if method in self._event_method_names:
            event = self._event_method_names[method]
            for callback in self._event_callbacks[event]:
                try:
                    result = await callback(**params)
                except Exception:
                    _error(f'Error occurred while running event callback '
                           f'{event} of script {self._name}:')
                    _error(format_exc())
                    result = None
            return result
        raise MethodNotFoundError()

    async def _register(self) -> None:
        self._auth_key = await self._call_api(
            'registerScript',
            script=self._name,
            description=self._description,
            permissionLevel=self._permission_level
        )
        for event in self._event_callbacks:
            await self._call_api(
                'registerCallback',
                event={'name': event.name, 'param': event.param},
                callback=event.to_method_name(self)
            )

    async def _deregister(self) -> None:
        if self._auth_key != '':
            await self._call_api('deregisterScript')
            self._auth_key = ''

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, Script)
                and self.name == other.name
                and self._auth_key == other._auth_key)

    def __hash__(self) -> int:
        return hash((self.name, self._auth_key))

    async def gametime(self) -> int:
        '''Get current monotonic world time of the overworld in gametick.

        Guaranteed to return the same value as player executes
        `/time query gametime` in the same gametick. Also guaranteed to return
        the same value when called from callbacks of event onGametickStart and
        onGametickEnd in the same gametick.'''
        return await self._call_api('gametime')

    async def read_interface(self, interface: Interface) -> int:
        '''Read from the interface.'''
        return _base64_to_int(
            await self._call_api('readInterface', interface=interface.name))

    async def read_interface_bytes(self, interface: Interface) -> bytes:
        '''Read from the interface.'''
        return _base64_to_bytes(
            await self._call_api('readInterface', interface=interface.name))

    async def write_interface(self, interface: Interface,
                              data: int | bytes) -> None:
        '''Write to the interface.'''
        if isinstance(data, bytes):
            await self._call_api(
                'writeInterface',
                interface=interface.name,
                data=_bytes_to_base64(data))
        elif isinstance(data, int):
            await self._call_api(
                'writeInterface',
                interface=interface.name,
                data=_int_to_base64(data))

    async def info(self, message: str) -> None:
        '''Log the message as information.'''
        await self._call_api('info', message=message)

    async def warn(self, message: str) -> None:
        '''Log the message as warning.'''
        await self._call_api('warn', message=message)

    async def error(self, message: str) -> None:
        '''Log the message as error.'''
        await self._call_api('error', message=message)

    @property
    def on_gametick_start(self) -> Callable[[Callable[[], Awaitable[None]]],
                                            Callable[[], Awaitable[None]]]:
        '''Called at the start of every gametick.'''
        return self._event_callback_registerer(_Events.ON_GAMETICK_START)
