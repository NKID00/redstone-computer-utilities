from typing import (Any, Awaitable, Callable, Dict, List, Optional, TypeVar,
                    cast)
import asyncio

from .io import MethodNotFoundError, _JsonRpcIO

_C = TypeVar('_C', bound=Callable[..., Awaitable[Any]])


class Script:
    '''Script'''

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
        self._event_callbacks: Dict[str, List[Callable[..., Awaitable]]] = {}
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

    async def _call_api(self, method: str,
                        params: Dict[str, Any] = None) -> Any:
        if params is None:
            params = {}
        if self._auth_key != '':
            params['authKey'] = self._auth_key
        return await cast(_JsonRpcIO, self._io).send(method, params, self._id)

    def _register_event_callback(self, event: str, callback: _C) -> _C:
        if event not in self._event_callbacks:
            self._event_callbacks[event] = []
        self._event_callbacks[event].append(callback)
        return callback

    def _event_callback_registerer(self, event: str) -> Callable[[_C], _C]:
        return lambda callback: self._register_event_callback(event, callback)

    async def _dispatch_request(self, method: str, params: Dict[str, Any]
                                ) -> Any:
        if method.startswith(self._name + '_'):
            event = method.split('_', 1)[1]
            if event in self._event_callbacks:
                for callback in self._event_callbacks[event]:
                    result = await callback(**params)
                return result
        raise MethodNotFoundError()

    async def _register(self) -> None:
        self._auth_key = await self._call_api('registerScript', {
            'script': self._name,
            'description': self._description,
            'permissionLevel': self._permission_level
        })
        for event in self._event_callbacks:
            await self._call_api('registerCallback', {
                'event': event,
                'callback': f'{self._name}_{event}'
            })

    async def _deregister(self) -> None:
        if self._auth_key != '':
            await self._call_api('deregisterScript', {
                'authKey': self._auth_key
            })
            self._auth_key = ''

    async def gametime(self) -> int:
        '''Get current monotonic world time of the overworld in gametick.

        Guaranteed to return the same value as player executes
        `/time query gametime` in the same gametick. Also guaranteed to return
        the same value when called from callbacks of event onGametickStart and
        onGametickEnd in the same gametick.'''
        return await self._call_api('gametime')

    @property
    def on_gametick_start(self) -> Callable[[Callable[[], Awaitable[None]]],
                                            Callable[[], Awaitable[None]]]:
        '''Called at the start of every gametick.'''
        return self._event_callback_registerer('onGametickStart')
