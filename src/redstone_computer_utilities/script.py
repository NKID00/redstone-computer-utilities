from __future__ import annotations
from traceback import format_exc
from typing import Any, Awaitable, Callable, Iterable, Optional, TypeVar, cast
import asyncio
from uuid import UUID

from redstone_computer_utilities.interval import Interval

from .io import MethodNotFoundError, _JsonRpcIO
from .event import Event, _SimpleEvent, _Events
from .interface import Interface
from .pos import Vec3i
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
            Event, list[Callable[..., Awaitable]]] = {}
        self._event_method_names: dict[str, Event] = {}
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

    def _register_event_callback(self, event: Event, callback: _C) -> _C:
        if event not in self._event_callbacks:
            self._event_callbacks[event] = []
        self._event_callbacks[event].append(callback)
        self._event_method_names[event.to_method_name(self)] = event
        return callback

    def _event_callback_registerer(self, event: Event) -> Callable[[_C], _C]:
        return lambda callback: self._register_event_callback(event, callback)

    async def _dispatch_request(self, method: str, params: dict[str, Any]
                                ) -> Any:
        if method in self._event_method_names:
            key_map = {
                'authKey': 'auth_key'
            }
            params = {key_map[k] if k in key_map else k: params[k]
                      for k in params}
            event = self._event_method_names[method]
            for callback in self._event_callbacks[event]:
                try:
                    result = await callback(**params)
                except Exception:  # pylint: disable=broad-except
                    _error(f'Error occurred while running event callback '
                           f'{event} of script {self._name}:')
                    _error(format_exc())
                    result = None
            return result
        raise MethodNotFoundError()

    async def _register(self) -> None:
        method = f'{self._name}_onScriptRegister'
        event = _SimpleEvent('onScriptRegister')
        self._event_callbacks[event] = [self._on_script_register]
        self._event_method_names[method] = event
        await self._call_api(
            'registerScript',
            script=self._name,
            description=self._description,
            permissionLevel=self._permission_level,
            callback=method
        )

    async def _on_script_register(self, auth_key: str) -> None:
        method = f'{self._name}_onScriptRegister'
        event = self._event_method_names[method]
        del self._event_callbacks[event]
        del self._event_method_names[method]
        self._auth_key = auth_key
        for event in self._event_callbacks:
            await self._call_api(
                'registerCallback',
                event={'name': event.name, 'param': event.serializable_param},
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

    async def list_script(self) -> dict[str, Script]:
        '''List registered scripts.'''
        return {k: Script(k, v['description'], v['permissionLevel'])
                for k, v in (await self._call_api('listScript')).items()}

    async def invoke_script(self, script: str | Script, **kwargs) -> Any:
        '''Invoke the script.'''
        if isinstance(script, Script):
            script = script.name
        return await self._call_api('invokeScript', script=script, args=kwargs)

    async def list_callback(self, script: str | Script) -> list[Event]:
        '''(experimental) List registered event callbacks of the script.'''
        if isinstance(script, Script):
            script = script.name
        return [Event(item['name'], item['param']) for item in
                await self._call_api('listCallback', script=script)]

    async def invoke_callback(self, script: str | Script, event: str | Event,
                              **kwargs) -> Any:
        '''(experimental) Invoke the event callback of the script.'''
        if isinstance(script, Script):
            script = script.name
        if isinstance(event, Event):
            event_dict = {'name': event.name,
                          'param': event.serializable_param}
        else:
            event_dict = {'name': event, 'param': None}
        return await self._call_api('listCallback', script=script,
                                    event=event_dict, args=kwargs)

    async def new_interface(self, name: str, lsb: Iterable[int] | Vec3i,
                            increment: Iterable[int] | Vec3i, size: int,
                            world: str = 'minecraft:overworld',
                            **kwargs) -> Interface:
        '''Create an interface.'''
        if not isinstance(lsb, Vec3i):
            lsb = Vec3i.from_iterable(lsb)
        if not isinstance(increment, Vec3i):
            increment = Vec3i.from_iterable(increment)
        await self._call_api('newInterface', interface=name,
                             world=world, lsb=[lsb.x, lsb.y, lsb.z],
                             increment=[increment.x, increment.y, increment.z],
                             size=size, args=kwargs)
        return Interface(name, self)

    async def remove_interface(self, interface: str | Interface) -> None:
        '''Remove the interface.'''
        if isinstance(interface, Interface):
            interface = interface.name
        await self._call_api('removeInterface', interface=interface)

    async def list_interface(self) -> dict[str, tuple[Interface, str, Vec3i,
                                                      Vec3i, int]]:
        '''List interfaces. Values of the result is a tuple (interface handle,
        world, lsb, increment, size).'''
        return {k: (Interface(k, self), v['world'],
                    Vec3i.from_iterable(v['lsb']),
                    Vec3i.from_iterable(v['increment']), v['size'])
                for k, v in (await self._call_api('listInterface')).items()}

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

    async def gametime(self) -> int:
        '''Get current monotonic world time of the overworld in gametick.

        Guaranteed to return the same value as player executes
        `/time query gametime` in the same gametick. Also guaranteed to return
        the same value when called from callbacks of event onGametickStart and
        onGametickEnd in the same gametick.'''
        return await self._call_api('gametime')

    async def list_player(self) -> dict[str, tuple[UUID, int]]:
        '''List interfaces. Values of the result is a tuple (uuid, permission
        level).'''
        return {k: (UUID(v['uuid']), v['permissionLevel'])
                for k, v in (await self._call_api('listPlayer')).items()}

    async def info(self, message: str) -> None:
        '''Log the message as information.'''
        await self._call_api('info', message=message)

    async def warn(self, message: str) -> None:
        '''Log the message as warning.'''
        await self._call_api('warn', message=message)

    async def error(self, message: str) -> None:
        '''Log the message as error.'''
        await self._call_api('error', message=message)

    async def send_info(self, uuid: str | UUID, message: str) -> None:
        '''Send the message as information to the player.'''
        if isinstance(uuid, UUID):
            uuid = str(uuid)
        await self._call_api('sendInfo', uuid=uuid, message=message)

    async def send_warn(self, uuid: str | UUID, message: str) -> None:
        '''Send the message as warning to the player.'''
        if isinstance(uuid, UUID):
            uuid = str(uuid)
        await self._call_api('sendWarn', uuid=uuid, message=message)

    async def send_error(self, uuid: str | UUID, message: str) -> None:
        '''Send the message as error to the player.'''
        if isinstance(uuid, UUID):
            uuid = str(uuid)
        await self._call_api('sendError', uuid=uuid, message=message)

    @property
    def on_gametick_start(self) -> Callable[[Callable[[], Awaitable[None]]],
                                            Callable[[], Awaitable[None]]]:
        '''Called at the start of every gametick.'''
        return self._event_callback_registerer(_Events.ON_GAMETICK_START)

    @property
    def on_gametick_end(self) -> Callable[[Callable[[], Awaitable[None]]],
                                          Callable[[], Awaitable[None]]]:
        '''Called at the end of every gametick.'''
        return self._event_callback_registerer(_Events.ON_GAMETICK_END)

    def on_gametick_start_delay(self, interval: Interval) -> Callable[
            [Callable[[], Awaitable[None]]], Callable[[], Awaitable[None]]]:
        '''Called only once at the start of the gametick after delay
        interval.'''
        return self._event_callback_registerer(
            _Events.ON_GAMETICK_START_DELAY.with_interval(interval))

    def on_gametick_end_delay(self, interval: Interval) -> Callable[
            [Callable[[], Awaitable[None]]], Callable[[], Awaitable[None]]]:
        '''Called only once at the end of the gametick after delay
        interval.'''
        return self._event_callback_registerer(
            _Events.ON_GAMETICK_END_DELAY.with_interval(interval))

    def on_gametick_start_clock(self, interval: Interval) -> Callable[
            [Callable[[], Awaitable[None]]], Callable[[], Awaitable[None]]]:
        '''Called at the start of the gametick for every clock cycle.'''
        return self._event_callback_registerer(
            _Events.ON_GAMETICK_START_CLOCK.with_interval(interval))

    def on_gametick_end_clock(self, interval: Interval) -> Callable[
            [Callable[[], Awaitable[None]]], Callable[[], Awaitable[None]]]:
        '''Called at the end of the gametick for every clock cycle.'''
        return self._event_callback_registerer(
            _Events.ON_GAMETICK_END_CLOCK.with_interval(interval))
