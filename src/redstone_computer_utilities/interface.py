from __future__ import annotations
from typing import Optional

import redstone_computer_utilities as rcu


class Interface:
    '''Interface. Only represents a handle that may bound to a script, the
    actual interface is never processed locally.'''

    def __init__(self, name: str,
                 script: Optional[rcu.Script] = None) -> None:
        self._name = name
        self._script = script

    @property
    def name(self) -> str:
        return self._name

    @property
    def script(self) -> Optional[rcu.Script]:
        return self._script

    def __eq__(self, other: object) -> bool:
        return (isinstance(other, Interface)
                and self._name == other._name)

    def __hash__(self) -> int:
        return hash((self._name,))

    def with_script(self, script: rcu.Script) -> Interface:
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

    async def write(self, data: int | bytes) -> None:
        '''Write to the interface.'''
        if self._script is None:
            return
        await self._script.write_interface(self, data)
