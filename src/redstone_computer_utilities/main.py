from __future__ import annotations
from collections import deque
from itertools import zip_longest
from traceback import format_exc, format_exception
import asyncio
from typing import (Any, Callable, Coroutine, Iterable, NoReturn, Optional,
                    Type, TypeVar, Union, cast)
from uuid import UUID
import inspect
from inspect import Parameter
try:
    from types import UnionType, NoneType  # type:ignore
except ImportError:  # Python < 3.10
    UnionType = None  # type:ignore
    NoneType = type(None)  # type:ignore

import typing_extensions
from typing_extensions import Protocol

from .interval import Interval
from .timer import Timer
from .task import TaskManager
from .io import JsonRpcIO, MethodNotFoundError, ResponseError, ResponseErrors
from .pos import Vec3i
from .cli import cli_init, info, warn, error, spinner
from .util import (base64_to_int, base64_to_bytes,
                   int_to_base64, bytes_to_base64)


class Event:
    '''Event.'''

    def __init__(self, name: str, param: Any = None) -> None:
        self._name = name
        self._param = param

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, Event)
                and self._name == other._name
                and self._param == other._param)

    def __hash__(self) -> int:
        return hash((self._name, self.serializable_param))

    def __str__(self) -> str:
        if self._param is None:
            return self._name
        return f'{self._name}({self.serializable_param})'

    def __repr__(self) -> str:
        if self._param is None:
            return f'Event({self._name!r})'
        return f'Event({self._name!r}, {self._param!r})'

    @property
    def name(self) -> str:
        return self._name

    @property
    def param(self) -> Any:
        return self._param

    def to_method_name(self, script: Script) -> str:
        if self._param is not None:
            return f'{script.name}_{self._name}_{self.serializable_param}'
        return f'{script.name}_{self._name}'

    @property
    def serializable_param(self) -> Any:
        return self.param


class SimpleEvent(Event):
    def __init__(self, name: str) -> None:
        super().__init__(name, None)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, SimpleEvent) and super().__eq__(other)

    def __hash__(self) -> int:
        return hash((self._name, self.serializable_param))

    def __repr__(self) -> str:
        return f'SimpleEvent({self._name!r})'


class InterfaceEvent(Event):
    def __init__(self, name: str,
                 interface: Optional[Interface] = None):
        super().__init__(name, interface)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, InterfaceEvent) and super().__eq__(other)

    def __hash__(self) -> int:
        return hash((self._name, self.serializable_param))

    def __repr__(self) -> str:
        if self._param is None:
            return f'InterfaceEvent({self._name!r})'
        return f'InterfaceEvent({self._name!r}, {self._param!r})'

    def with_interface(self, interface: Interface
                       ) -> InterfaceEvent:
        return InterfaceEvent(self.name, interface)

    @property
    def serializable_param(self) -> str:
        return self.param.name


class TimedEvent(Event):
    def __init__(self, name: str,
                 interval: Optional[Interval] = None):
        super().__init__(name, interval)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, TimedEvent) and super().__eq__(other)

    def __hash__(self) -> int:
        return hash((self._name, self.serializable_param))

    def __repr__(self) -> str:
        if self._param is None:
            return f'TimedEvent({self._name!r})'
        return f'TimedEvent({self._name!r}, {self._param!r})'

    def with_interval(self, interval: Interval
                      ) -> TimedEvent:
        return TimedEvent(self.name, interval)

    @property
    def serializable_param(self) -> int:
        return self.param.gametick


class Events:
    ON_SCRIPT_REGISTER = SimpleEvent('onScriptRegister')
    ON_SCRIPT_RELOAD = SimpleEvent('onScriptReload')
    ON_SCRIPT_RUN = SimpleEvent('onScriptRun')
    ON_SCRIPT_INVOKE = SimpleEvent('onScriptInvoke')
    ON_GAMETICK_START = SimpleEvent('onGametickStart')
    ON_GAMETICK_END = SimpleEvent('onGametickEnd')
    ON_INTERFACE_NEW = SimpleEvent('onInterfaceNew')
    ON_GAMETICK_START_DELAY = TimedEvent('onGametickStartDelay')
    ON_GAMETICK_END_DELAY = TimedEvent('onGametickEndDelay')
    ON_GAMETICK_START_CLOCK = TimedEvent('onGametickStartClock')
    ON_GAMETICK_END_CLOCK = TimedEvent('onGametickEndClock')
    ON_INTERFACE_UPDATE = InterfaceEvent('onInterfaceUpdate')
    ON_INTERFACE_UPDATE_IMMEDIATE = InterfaceEvent(
        'onInterfaceUpdateImmediate')
    ON_INTERFACE_READ = InterfaceEvent('onInterfaceRead')
    ON_INTERFACE_WRITE = InterfaceEvent('onInterfaceWrite')
    ON_INTERFACE_REMOVE = InterfaceEvent('onInterfaceRemove')


class Interface:
    '''Interface. Only represents a handle that may bound to a script, the
    actual interface is never processed locally.'''

    def __init__(self, name: str,
                 script: Optional[Script] = None) -> None:
        self._name = name
        self._script = script

    @property
    def name(self) -> str:
        return self._name

    @property
    def script(self) -> Optional[Script]:
        return self._script

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, Interface)
                and self._name == other._name)

    def __hash__(self) -> int:
        return hash((self._name,))

    def __str__(self) -> str:
        return f'interface:{self._name}'

    def __repr__(self) -> str:
        if self._script is None:
            return f'Interface({self._name!r})'
        else:
            return f'Interface({self._name!r}, {self._script!r})'

    def with_script(self, script: Script) -> Interface:
        '''Create a new interface handle that bounds to the given script.'''
        return Interface(self._name, script)

    async def read(self) -> int:
        '''Read from the interface.'''
        if self._script is None:
            return 0
        return await self._script.read_interface(self)

    async def read_bytes(self) -> bytes:
        '''Read from the interface.'''
        if self._script is None:
            return b''
        return await self._script.read_interface_bytes(self)

    async def write(self, data: Union[int, bytes]) -> None:
        '''Write to the interface.'''
        if self._script is None:
            return
        await self._script.write_interface(self, data)


def dict_to_type(v: dict[str, str]) -> Type:
    type_ = v['type']
    if type_ == 'interface':
        return Interface
    elif type_ == 'script':
        return Script
    else:  # literal
        return str


def dict_to_arg(v: dict[str, str], script: Script
                ) -> Union[str, Interface, Script]:
    type_, value = v['type'], v['value']
    if type_ == 'interface':
        return Interface(value).with_script(script)
    elif type_ == 'script':
        return Script(value)
    else:  # literal
        return value


def arg_to_dict(v: Union[str, Interface, Script]) -> dict[str, str]:
    if isinstance(v, Interface):
        type_, value = 'interface', v.name
    elif isinstance(v, Script):
        type_, value = 'script', v.name
    else:  # str
        type_, value = 'literal', v
    return {'type': type_, 'value': value}


def is_union(t: Type) -> bool:
    origin = typing_extensions.get_origin(t)
    if origin is Union:
        return True
    elif UnionType is not None and origin is UnionType:
        return True
    else:
        return False


def get_union_args(t: Type) -> tuple[Type, ...]:
    if t == Parameter.empty:
        return ()
    elif is_union(t):
        return typing_extensions.get_args(t)
    else:
        return (t,)


class NoArgCallback(Protocol):
    def __call__(self) -> Coroutine[Any, Any, None]: ...


class InterfaceArgCallback(Protocol):
    def __call__(self, interface: Interface) -> Coroutine[Any, Any, None]: ...


class ScriptArgCallback(Protocol):
    def __call__(self, script: Script) -> Coroutine[Any, Any, None]: ...


class RunCallback(Protocol):
    def __call__(self, uuid: UUID, run_args: list[dict[str, str]]
                 ) -> Coroutine[Any, Any, int]: ...


class InvokeCallback(Protocol):
    def __call__(self, script: Script, args: dict[str, Any]
                 ) -> Coroutine[Any, Any, Any]: ...


AnyCallback = Callable[..., Coroutine]
# there's no way to define a type to constrain the params of main callback :(
MainRetValT = TypeVar('MainRetValT', int, None,
                      Optional[int])
MainCallback = Callable[..., Coroutine[Any, Any, MainRetValT]]
CallbackT = TypeVar('CallbackT', bound=AnyCallback)
StaticCallbackRegisterer = Callable[[CallbackT], CallbackT]
RuntimeCallbackRegisterer = Callable[
    [CallbackT], Coroutine[Any, Any, CallbackT]]
CallbackRegisterer = Callable[[CallbackT], Any]


class Script:
    '''May represents a handle only or a running script with callable apis,
    which can be inspected with the member ``running``. It is recommended to
    use ``create_script`` rather than register manually.'''

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
            Event, list[Union[AnyCallback, Timer]]] = {}
        self._main_callbacks: list[MainCallback] = []
        self._event_method_names: dict[str, Event] = {}
        self._io: JsonRpcIO = cast(JsonRpcIO, None)
        self._id_lock: asyncio.Lock = asyncio.Lock()
        self._id_value: int = 0
        self._auth_key: str = ''
        self._detached_tasks: set[asyncio.Task] = set()
        self._detach_task: asyncio.Task = cast(asyncio.Task, None)
        self._detach_event: asyncio.Event = cast(asyncio.Event, None)
        self._task_manager: TaskManager = cast(
            TaskManager, None)
        self._running: bool = False

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, Script)
                and self._name == other._name)

    def __hash__(self) -> int:
        return hash((self._name,))

    def __str__(self) -> str:
        return f'script:{self._name}'

    def __repr__(self) -> str:
        return f'Script({self._name!r}, ...)'

    @property
    def name(self) -> str:
        return self._name

    def _set_internal(self, io: JsonRpcIO, task_manager: TaskManager):
        self._io = io
        self._task_manager = task_manager
        self._detach_event = task_manager.event()
        self._running = True

    @property
    def running(self) -> bool:
        return self._running

    @property
    def _id(self) -> str:
        self._id_value += 1
        return f'c_{self._name}_{self._id_value}'

    async def _call_api(self, method: str, **params: Any) -> Any:
        if params is None:
            params = {}
        if self._auth_key != '':
            params['authKey'] = self._auth_key
        return await self._io.send(method, params, self._id)

    def _is_event_registered(self, event: Event) -> bool:
        return (event in self._event_callbacks
                and len(self._event_callbacks[event]) > 0)

    def _add_event_callback(self, event: Event,
                            callback: Union[AnyCallback, Timer]
                            ) -> Union[AnyCallback, Timer]:
        if event not in self._event_callbacks:
            self._event_callbacks[event] = []
        self._event_callbacks[event].append(callback)
        self._event_method_names[event.to_method_name(self)] = event
        return callback

    async def _register_event(self, event: Event) -> None:
        await self._call_api(
            'registerCallback',
            event={'name': event.name,
                   'param': event.serializable_param},
            callback=event.to_method_name(self))

    async def _register_event_callback(self, event: Event,
                                       callback: Union[AnyCallback, Timer]
                                       ) -> Union[AnyCallback, Timer]:
        if not self._is_event_registered(event):
            await self._register_event(event)
        self._add_event_callback(event, callback)
        return callback

    def _remove_event_callback(self, event: Event) -> None:
        del self._event_callbacks[event]
        del self._event_method_names[event.to_method_name(self)]

    def _remove_callback(self, event: Event,
                         callback: Union[AnyCallback, Timer]) -> None:
        if self._is_event_registered(event):
            self._event_callbacks[event].remove(callback)

    async def _deregister_event(self, event: Event):
        await self._call_api(
            'deregisterCallback',
            event={'name': event.name,
                   'param': event.serializable_param})

    async def _deregister_event_callbacks(self, event: Event) -> None:
        self._remove_event_callback(event)
        await self._deregister_event(event)

    async def _deregister_callback(self, callback: Union[AnyCallback, Timer]
                                   ) -> None:
        for event, callback_list in list(self._event_callbacks.items()):
            if callback in callback_list:
                callback_list.remove(callback)
                if len(callback_list) == 0:
                    await self._deregister_event_callbacks(event)
        if not isinstance(callback, Timer):
            if callback in self._main_callbacks:
                self._main_callbacks.remove(callback)
                if len(self._main_callbacks) == 0:
                    await self._deregister_callback(self._dispatch_main)

    def _callback_registerer(self, event: Event
                             ) -> CallbackRegisterer[AnyCallback]:
        if self._running:
            return cast(RuntimeCallbackRegisterer[AnyCallback],
                        lambda callback: self._register_event_callback(
                            event, callback))
        else:
            return cast(StaticCallbackRegisterer[AnyCallback],
                        lambda callback: self._add_event_callback(
                            event, callback))

    async def _dispatch_request(self, method: str, params: dict[str, Any]
                                ) -> Any:
        if method in self._event_method_names:
            mapped_params = {}
            key_map = {
                'authKey': 'auth_key',
                'runArgs': 'run_args'
            }
            for k, v in params.items():
                if k in key_map:
                    k = key_map[k]
                elif k == 'interface':
                    v = Interface(v, self)
                elif k == 'script':
                    v = Script(v)
                elif k == 'uuid':
                    if v is not None:
                        v = UUID(v)
                mapped_params[k] = v
            event = self._event_method_names[method]

            async def call_suppress(callback, **kwargs) -> Any:
                try:
                    return await callback(**kwargs)
                except ResponseError as exc:
                    if exc.get_id() is None:
                        raise
                    else:
                        # `raise from` will modify the exception
                        raise (ResponseErrors.SCRIPT_INTERNAL_ERROR
                               .with_cause(exc))
                except Exception:  # pylint: disable=broad-except
                    error(f'Error occurred while running event callback '
                          f'{event} of {self}:')
                    error(format_exc())

            result: Any = None
            response_error: Optional[ResponseError] = None
            last_detach_task = self._detach_task
            for callback in self._event_callbacks[event].copy():
                self._detach_event.clear()
                if isinstance(callback, Timer):
                    self._detach_task = callback.task
                    self._remove_callback(event, callback)
                    callback.event.set()
                else:
                    self._detach_task = self._task_manager.create_task(
                        call_suppress(callback, **mapped_params))
                await asyncio.wait(
                    (self._detach_event.wait(), self._detach_task),
                    return_when=asyncio.FIRST_COMPLETED)
                if self._detach_task.done():
                    try:
                        result = self._detach_task.result()
                    except ResponseError as exc:
                        if exc == ResponseErrors.SCRIPT_INTERNAL_ERROR:
                            error(f'Script internal error occurred while '
                                  f'running event callback {event} of {self}:')
                            error(''.join(format_exception(
                                type(exc), exc, exc.__traceback__)))
                        response_error = exc
                else:
                    self._task_manager.add_task(self._detach_task)
            self._detach_task = last_detach_task
            if response_error is not None:
                raise response_error
            return result
        raise MethodNotFoundError()

    async def _register(self) -> None:
        event = Events.ON_SCRIPT_REGISTER
        self._add_event_callback(event, self._on_script_register)
        await self._call_api(
            'registerScript',
            script=self._name,
            description=self._description,
            permissionLevel=self._permission_level,
            callback=event.to_method_name(self))

    async def _on_script_register(self, auth_key: str) -> None:
        self._auth_key = auth_key
        for event in self._event_callbacks:
            if event != Events.ON_SCRIPT_REGISTER:
                await self._register_event(event)

    async def _deregister(self) -> None:
        if self._auth_key != '':
            await self._call_api('deregisterScript')
            self._auth_key = ''

    def _detach(self) -> None:
        self._detach_event.set()

    async def deregister_callback(self, callback: AnyCallback) -> AnyCallback:
        '''Deregister registered event callback.'''
        await self._deregister_callback(callback)
        return callback

    async def list_script(self) -> dict[str, Script]:
        '''List registered scripts.'''
        return {k: Script(k, v['description'], v['permissionLevel'])
                for k, v in (await self._call_api('listScript')).items()}

    async def invoke_script(self, script: Union[str, Script], **kwargs: Any
                            ) -> Any:
        '''Invoke the script.'''
        if isinstance(script, Script):
            script = script.name
        return await self._call_api('invokeScript', script=script, args=kwargs)

    async def list_callback(self, script: Union[str, Script]) -> list[Event]:
        '''(experimental) List registered event callbacks of the script.'''
        if isinstance(script, Script):
            script = script.name
        return [Event(item['name'], item['param']) for item in
                await self._call_api('listCallback', script=script)]

    async def invoke_callback(self, script: Union[str, Script],
                              event: Union[str, Event],
                              args: Optional[dict[str, Any]] = None) -> Any:
        '''(experimental) Invoke the event callback of the script.'''
        if isinstance(script, Script):
            script = script.name
        if isinstance(event, Event):
            event_dict = {'name': event.name,
                          'param': event.serializable_param}
        else:
            event_dict = {'name': event, 'param': None}
        if args is None:
            args = {}
        return await self._call_api('listCallback', script=script,
                                    event=event_dict, args=args)

    async def new_interface(self, name: str, lsb: Union[Iterable[int], Vec3i],
                            increment: Union[Iterable[int], Vec3i], size: int,
                            world: str = 'minecraft:overworld',
                            args: Optional[dict[str, Any]] = None
                            ) -> Interface:
        '''Create an interface.'''
        if not isinstance(lsb, Vec3i):
            lsb = Vec3i.from_iterable(lsb)
        if not isinstance(increment, Vec3i):
            increment = Vec3i.from_iterable(increment)
        if args is None:
            args = {}
        await self._call_api('newInterface', interface=name,
                             world=world, lsb=[lsb.x, lsb.y, lsb.z],
                             increment=[increment.x, increment.y, increment.z],
                             size=size, args=args)
        return Interface(name, self)

    async def remove_interface(self, interface: Union[str, Interface]) -> None:
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
        return base64_to_int(
            await self._call_api('readInterface', interface=interface.name))

    async def read_interface_bytes(self, interface: Interface) -> bytes:
        '''Read from the interface.'''
        return base64_to_bytes(
            await self._call_api('readInterface', interface=interface.name))

    async def write_interface(self, interface: Interface,
                              data: Union[int, bytes]) -> None:
        '''Write to the interface.'''
        if isinstance(data, bytes):
            await self._call_api(
                'writeInterface',
                interface=interface.name,
                data=bytes_to_base64(data))
        elif isinstance(data, int):
            await self._call_api(
                'writeInterface',
                interface=interface.name,
                data=int_to_base64(data))

    async def gametime(self) -> int:
        '''Get current monotonic world time of the overworld in gametick.

        Guaranteed to return the same value as player executes
        `/time query gametime` in the same gametick. Also guaranteed to return
        the same value when called from callbacks of event onGametickStart and
        onGametickEnd in the same gametick.'''
        return await self._call_api('gametime')

    async def list_player(self) -> dict[UUID, tuple[str, int]]:
        '''(experimental) List online players. Values of the result is a tuple
        (name, permission level).'''
        return {UUID(k): (v['name'], v['permissionLevel'])
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

    async def send_info(self, uuid: UUID, message: str) -> None:
        '''Send the message as information to the player.'''
        await self._call_api('sendInfo', uuid=str(uuid), message=message)

    async def send_warn(self, uuid: UUID, message: str) -> None:
        '''Send the message as warning to the player.'''
        await self._call_api('sendWarn', uuid=str(uuid), message=message)

    async def send_error(self, uuid: UUID, message: str) -> None:
        '''Send the message as error to the player.'''
        await self._call_api('sendError', uuid=str(uuid), message=message)

    @property
    def on_script_register(self) -> CallbackRegisterer[NoArgCallback]:
        '''Called when the scripts is almost registered.'''
        return self._callback_registerer(Events.ON_SCRIPT_REGISTER)

    @property
    def on_script_reload(self) -> CallbackRegisterer[NoArgCallback]:
        '''Called when `/rcu reload` is executed.'''
        return self._callback_registerer(Events.ON_SCRIPT_RELOAD)

    @property
    def on_script_run(self) -> CallbackRegisterer[RunCallback]:
        '''Called when `/rcu run` is executed.'''
        return self._callback_registerer(Events.ON_SCRIPT_RUN)

    @property
    def on_script_invoke(self) -> CallbackRegisterer[InvokeCallback]:
        '''Called when invoked by another script.'''
        return self._callback_registerer(Events.ON_SCRIPT_INVOKE)

    @property
    def on_gametick_start(self) -> CallbackRegisterer[NoArgCallback]:
        '''Called at the start of every gametick.'''
        return self._callback_registerer(Events.ON_GAMETICK_START)

    @property
    def on_gametick_end(self) -> CallbackRegisterer[NoArgCallback]:
        '''Called at the end of every gametick.'''
        return self._callback_registerer(Events.ON_GAMETICK_END)

    def on_gametick_start_delay(self, interval: Interval
                                ) -> CallbackRegisterer[NoArgCallback]:
        '''Called only once at the start of the gametick after delay
        interval.'''
        return self._callback_registerer(
            Events.ON_GAMETICK_START_DELAY.with_interval(interval))

    def on_gametick_end_delay(self, interval: Interval
                              ) -> CallbackRegisterer[NoArgCallback]:
        '''Called only once at the end of the gametick after delay
        interval.'''
        return self._callback_registerer(
            Events.ON_GAMETICK_END_DELAY.with_interval(interval))

    def on_gametick_start_clock(self, interval: Interval
                                ) -> CallbackRegisterer[NoArgCallback]:
        '''Called at the start of the gametick for every clock cycle.'''
        return self._callback_registerer(
            Events.ON_GAMETICK_START_CLOCK.with_interval(interval))

    def on_gametick_end_clock(self, interval: Interval
                              ) -> CallbackRegisterer[NoArgCallback]:
        '''Called at the end of the gametick for every clock cycle.'''
        return self._callback_registerer(
            Events.ON_GAMETICK_END_CLOCK.with_interval(interval))

    def on_interface_update(self, interface: Interface
                            ) -> CallbackRegisterer[NoArgCallback]:
        '''Called at the end of the gametick when redstone signal received by
        the interface is changed. Guaranteed to be called at most once within
        a gametick at the end of it.'''
        return self._callback_registerer(
            Events.ON_INTERFACE_UPDATE.with_interface(interface))

    def on_interface_update_immediate(self, interface: Interface
                                      ) -> CallbackRegisterer[NoArgCallback]:
        '''Called immediately when redstone signal received by the interface
        is changed. May be triggered multiple times within a single
        gametick.'''
        return self._callback_registerer(
            Events.ON_INTERFACE_UPDATE_IMMEDIATE.with_interface(interface))

    def on_interface_read(self, interface: Interface
                          ) -> CallbackRegisterer[ScriptArgCallback]:
        '''Called before the interface is read by a script.'''
        return self._callback_registerer(
            Events.ON_INTERFACE_READ.with_interface(interface))

    def on_interface_write(self, interface: Interface
                           ) -> CallbackRegisterer[ScriptArgCallback]:
        '''Called before the interface is written by a script.'''
        return self._callback_registerer(
            Events.ON_INTERFACE_WRITE.with_interface(interface))

    def on_interface_new(self, interface: Interface
                         ) -> CallbackRegisterer[InterfaceArgCallback]:
        '''(experimental) Called after an interface is created successfully.'''
        return self._callback_registerer(
            Events.ON_INTERFACE_UPDATE.with_interface(interface))

    def on_interface_remove(self, interface: Interface
                            ) -> CallbackRegisterer[NoArgCallback]:
        '''(experimental) Called before the interface is removed.'''
        return self._callback_registerer(
            Events.ON_INTERFACE_REMOVE.with_interface(interface))

    async def _dispatch_main(self, uuid: Optional[UUID],
                             run_args: list[dict[str, str]]) -> int:
        async def call_suppress(callback: MainCallback, *args: Any) -> int:
            try:
                result = await callback(*args)
            except ResponseError:
                raise
            except Exception:  # pylint: disable=broad-except
                error(f'Error occurred while running main callback of {self}:')
                error(format_exc())
                return 0
            else:
                if isinstance(result, int):
                    return result
                else:
                    return 0

        result: int = 0
        called = False
        response_error: Optional[ResponseError] = None
        last_detach_task = self._detach_task
        for callback in self._main_callbacks.copy():
            signature = inspect.signature(callback)
            args_wanted = list(signature.parameters.values())
            args: list[Union[Optional[UUID], str,
                             Interface, Script]] = []
            should_call = False
            if len(args_wanted) == 0:
                if len(run_args) == 0:
                    should_call = True
                else:
                    continue
            else:
                if args_wanted[0].annotation == Optional[UUID]:
                    args_wanted = args_wanted[1:]
                    args.append(uuid)
                if len(args_wanted) == len(run_args):
                    zipped: Iterable[tuple] = zip(args_wanted, run_args)
                elif args_wanted[-1].kind == Parameter.VAR_POSITIONAL:
                    if len(args_wanted) - 1 > len(run_args):
                        continue
                    zipped = zip_longest(
                        args_wanted, run_args,
                        fillvalue=args_wanted[-1])
                else:
                    continue
                for wanted, provided in zipped:
                    union_args = get_union_args(wanted.annotation)
                    if (len(union_args) == 0
                            or dict_to_type(provided) in union_args):
                        args.append(dict_to_arg(provided, self))
                    else:
                        break
                else:
                    should_call = True
            if not should_call:
                continue
            called = True
            self._detach_event.clear()
            self._detach_task = self._task_manager.create_task(
                call_suppress(callback, *args))
            await asyncio.wait(
                (self._detach_event.wait(), self._detach_task),
                return_when=asyncio.FIRST_COMPLETED)
            if self._detach_task.done():
                try:
                    result += self._detach_task.result()
                except ResponseError as exc:
                    response_error = exc
            else:
                self._task_manager.add_task(self._detach_task)
        if not called:
            raise ResponseErrors.ILLEGAL_ARGUMENT
        self._detach_task = last_detach_task
        if response_error is not None:
            raise response_error
        return result

    @staticmethod
    def _check_main(callback: MainCallback):
        signature = inspect.signature(callback)
        params = list(signature.parameters.values())
        if len(params) > 0:
            param0 = params[0]
            if param0.kind in (Parameter.POSITIONAL_OR_KEYWORD,
                               Parameter.POSITIONAL_ONLY):
                if param0.annotation == UUID:
                    raise TypeError(
                        f'inappropriate parameter {params[0]} for main '
                        f'function, use Optional[UUID] instead')
                elif param0.annotation == Optional[UUID]:
                    params = params[1:]
            for param in params:
                if param.kind in (Parameter.POSITIONAL_OR_KEYWORD,
                                  Parameter.POSITIONAL_ONLY,
                                  Parameter.VAR_POSITIONAL):
                    for anno in get_union_args(param.annotation):
                        if anno not in (str, Interface, Script):
                            raise TypeError(
                                f'Inappropriate parameter {param} for '
                                f'main function')
                else:
                    raise TypeError(
                        f'Inappropriate parameter {param} for main'
                        f'function, use positional argument instead')
        for anno in get_union_args(signature.return_annotation):
            if anno not in (int, None, NoneType, NoReturn):
                raise TypeError(
                    f'Inappropriate return type '
                    f'{signature.return_annotation} for main function')

    @property
    def main(self) -> CallbackRegisterer[MainCallback]:
        '''Called and dispatched when `/rcu run` is executed.'''
        async def runtime_registerer(callback: MainCallback) -> MainCallback:
            Script._check_main(callback)
            if len(self._main_callbacks) == 0:
                await self.on_script_run(self._dispatch_main)
            self._main_callbacks.append(callback)
            return callback

        def static_registerer(callback: MainCallback) -> MainCallback:
            Script._check_main(callback)
            if len(self._main_callbacks) == 0:
                self.on_script_run(self._dispatch_main)
            self._main_callbacks.append(callback)
            return callback

        if self._running:
            return runtime_registerer
        else:
            return static_registerer

    async def wait(self, interval: Interval) -> None:
        '''Suspend the execution until the interval elapses. Guaranteed to
        complete at the start of a gametick.'''
        await self.wait_gametick_start(interval)

    async def wait_gametick_start(self, interval: Interval) -> None:
        '''Suspend the execution until the interval elapses. Guaranteed to
        complete at the start of a gametick.'''
        event = self._task_manager.event()
        timer = Timer(event, self._detach_task)
        await self._register_event_callback(
            Events.ON_GAMETICK_START_DELAY.with_interval(interval), timer)
        self._detach()
        await event.wait()

    async def wait_gametick_end(self, interval: Interval) -> None:
        '''Suspend the execution until the interval elapses. Guaranteed to
        complete at the end of a gametick.'''
        event = self._task_manager.event()
        timer = Timer(event, self._detach_task)
        await self._register_event_callback(
            Events.ON_GAMETICK_END_DELAY.with_interval(interval), timer)
        self._detach()
        await event.wait()


_registered_scripts: list[Script] = []


def register_script(script: Script) -> None:
    '''Register the script. It is recommended to use ``create_script`` rather
    than register manually.'''
    _registered_scripts.append(script)


def create_script(name: str, description: str = '',
                  permission_level: int = 2) -> Script:
    '''Create a new script and register it.

    :param name: Name of the script. MUST be unique among all registered
    scripts and MUST be a string consists of only letters, numbers
    and underlines.
    :param description: Description of the script. SHOULD be any unicode
    string that can be displayed as plain text in Minecraft.
    :param permission_level: Permission level required to run the script.
    SHOULD be a integer >= 2 and <= 4.'''
    script = Script(name, description, permission_level)
    register_script(script)
    return script


def run(host: str = 'localhost', port: int = 37265,
        enable_builtins=False) -> None:
    '''Try to connect with script server and enter the main loop.'''
    cli_init()
    info('  Use Ctrl-C to exit')
    if enable_builtins:
        from . import builtins  # pylint: disable=unused-import
    while True:
        try:
            asyncio.run(run_async(host, port))
        except KeyboardInterrupt:
            break
        except asyncio.IncompleteReadError:
            pass
    info('  Stopped')


class EventTriggeredError(Exception):
    pass


async def run_async(host: str, port: int) -> None:
    async def dispatch_request(method: str, params: dict[str, Any]
                               ) -> Any:
        for script in _registered_scripts:
            try:
                # pylint: disable=protected-access
                return await script._dispatch_request(method, params)
            except MethodNotFoundError:
                continue
        raise MethodNotFoundError()

    task_manager = TaskManager(asyncio.get_running_loop())

    async with spinner(f'Connecting to script server {host}:{port}',
                       task_manager=task_manager):
        while True:
            try:
                io = JsonRpcIO(*await asyncio.open_connection(host, port),
                               dispatch_request, task_manager)
            except OSError:
                await asyncio.sleep(1)
            else:
                break
        info(f'Connected to script server {host}:{port}')

    async def task_added_event_listener() -> NoReturn:
        nonlocal task_manager
        await task_manager.task_added_event.wait()
        raise EventTriggeredError()

    async def watch_dog() -> NoReturn:
        nonlocal task_manager
        while True:
            _done, _pending = await asyncio.wait(
                task_manager.tasks.copy(),
                return_when=asyncio.FIRST_EXCEPTION)
            tasks_new: deque[asyncio.Task] = deque()
            for task in task_manager.tasks:
                try:
                    task.result()
                except asyncio.InvalidStateError:
                    tasks_new.append(task)
                    continue
                except EventTriggeredError:
                    task_manager.task_added_event.clear()
                    tasks_new.append(task_manager.create_task(
                        task_added_event_listener()))
                    continue
                except asyncio.IncompleteReadError:
                    warn('Disconnected')
                    raise
                except ResponseError:
                    pass
            task_manager.tasks.clear()
            task_manager.tasks.extend(tasks_new)

    task_manager.add_coros(task_added_event_listener(), io.run())
    watch_dog_task = task_manager.create_task(watch_dog())

    scripts_len = len(_registered_scripts)
    success_count = 0
    async with spinner(task_manager=task_manager) as wait:
        for i, script in enumerate(_registered_scripts):
            wait(f'Registering script(s) ({i}/{scripts_len})')
            # pylint: disable=protected-access
            script._set_internal(io, task_manager)
            try:
                await script._register()  # pylint: disable=protected-access
            except Exception:  # pylint: disable=broad-except
                error(f'Error occurred while registering {script}')
                error(format_exc())
            else:
                success_count += 1
                info(f'{script} is registered')
        info(f'Registered {success_count}/{scripts_len} script(s)')
    if success_count == 0:
        raise KeyboardInterrupt()

    async with spinner('Running', task_manager=task_manager) as wait:
        await watch_dog_task
        watch_dog_task.result()
