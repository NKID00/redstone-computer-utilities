from __future__ import annotations
from typing import Any, Optional

import redstone_computer_utilities as rcu


class _Event:
    def __init__(self, name: str, param: Optional[Any]) -> None:
        self.name = name
        self.param = param

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, _Event)
                and self.name == other.name
                and self.param == other.param)

    def __hash__(self) -> int:
        return hash((self.name, self.param))
    
    def __str__(self) -> str:
        if self.param is None:
            return self.name
        return f'{self.name}({self.param})'

    def to_method_name(self, script: rcu.script.Script) -> str:
        if self.param is not None:
            return f'{script.name}_{self.name}_{self.param}'
        else:
            return f'{script.name}_{self.name}'


class _SimpleEvent(_Event):
    def __init__(self, name: str) -> None:
        super().__init__(name, None)


class _InterfaceEvent(_Event):
    def __init__(self, name: str,
                 interface: Optional[rcu.interface.Interface] = None):
        super().__init__(name, interface)

    def with_interface(self, interface: rcu.interface.Interface
                       ) -> _InterfaceEvent:
        return _InterfaceEvent(self.name, interface)


class _Events:
    ON_SCRIPT_LOAD = _SimpleEvent('onScriptLoad')
    ON_SCRIPT_UNLOAD = _SimpleEvent('onScriptUnload')
    ON_SCRIPT_RUN = _SimpleEvent('onScriptRun')
    ON_SCRIPT_INVOKE = _SimpleEvent('onScriptInvoke')
    ON_GAMETICK_START = _SimpleEvent('onGametickStart')
    ON_GAMETICK_END = _SimpleEvent('onGametickEnd')
    ON_INTERFACE_REDSTONE_UPDATE = _InterfaceEvent('onInterfaceRedstoneUpdate')
    ON_INTERFACE_READ = _InterfaceEvent('onInterfaceRead')
    ON_INTERFACE_WRITE = _InterfaceEvent('onInterfaceWrite')
    ON_INTERFACE_NEW = _InterfaceEvent('onInterfaceNew')
    ON_INTERFACE_REMOVE = _InterfaceEvent('onInterfaceRemove')
