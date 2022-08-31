from __future__ import annotations
from typing import Any, Optional

import redstone_computer_utilities as rcu


class Event:
    '''Event.'''

    def __init__(self, name: str, param: Any) -> None:
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

    @property
    def name(self) -> str:
        return self._name

    @property
    def param(self) -> Any:
        return self._param

    def to_method_name(self, script: rcu.Script) -> str:
        if self._param is not None:
            return f'{script.name}_{self._name}_{self.serializable_param}'
        return f'{script.name}_{self._name}'

    @property
    def serializable_param(self) -> Any:
        return self.param


class _SimpleEvent(Event):
    def __init__(self, name: str) -> None:
        super().__init__(name, None)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, _SimpleEvent) and super().__eq__(other)

    def __hash__(self) -> int:
        return hash((self._name, self.serializable_param))


class _InterfaceEvent(Event):
    def __init__(self, name: str,
                 interface: Optional[rcu.Interface] = None):
        super().__init__(name, interface)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, _InterfaceEvent) and super().__eq__(other)

    def __hash__(self) -> int:
        return hash((self._name, self.serializable_param))

    def with_interface(self, interface: rcu.Interface
                       ) -> _InterfaceEvent:
        return _InterfaceEvent(self.name, interface)

    @property
    def serializable_param(self) -> str:
        return self.param.name


class _TimedEvent(Event):
    def __init__(self, name: str,
                 interval: Optional[rcu.Interval] = None):
        super().__init__(name, interval)

    def __eq__(self, other: object) -> bool:
        return isinstance(other, _TimedEvent) and super().__eq__(other)

    def __hash__(self) -> int:
        return hash((self._name, self.serializable_param))

    def with_interval(self, interval: rcu.Interval
                      ) -> _TimedEvent:
        return _TimedEvent(self.name, interval)

    @property
    def serializable_param(self) -> int:
        return self.param.gametick


class _Events:
    ON_SCRIPT_RELOAD = _SimpleEvent('onScriptReload')
    ON_SCRIPT_RUN = _SimpleEvent('onScriptRun')
    ON_SCRIPT_INVOKE = _SimpleEvent('onScriptInvoke')
    ON_GAMETICK_START = _SimpleEvent('onGametickStart')
    ON_GAMETICK_END = _SimpleEvent('onGametickEnd')
    ON_GAMETICK_START_DELAY = _TimedEvent('onGametickStartDelay')
    ON_GAMETICK_END_DELAY = _TimedEvent('onGametickEndDelay')
    ON_GAMETICK_START_CLOCK = _TimedEvent('onGametickStartClock')
    ON_GAMETICK_END_CLOCK = _TimedEvent('onGametickEndClock')
    ON_INTERFACE_REDSTONE_UPDATE = _InterfaceEvent('onInterfaceRedstoneUpdate')
    ON_INTERFACE_READ = _InterfaceEvent('onInterfaceRead')
    ON_INTERFACE_WRITE = _InterfaceEvent('onInterfaceWrite')
    ON_INTERFACE_NEW = _InterfaceEvent('onInterfaceNew')
    ON_INTERFACE_REMOVE = _InterfaceEvent('onInterfaceRemove')
