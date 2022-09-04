from typing import Optional
from uuid import UUID

import redstone_computer_utilities as rcu


script = rcu.create_script('main')


@script.main
# one argument
async def _(interface: rcu.Interface) -> int:
    print(f'{interface} @ without uuid')
    return 0  # return value may be int or None


@script.main
# uuid of command source and one argument
async def _(uuid: Optional[UUID], interface: rcu.Interface) -> Optional[int]:
    if uuid is None:
        print('executed by non-player')
    else:
        players = await script.list_player()
        print(f'executed by player {players[uuid][0]}')
    print(f'{interface} @ with uuid')
    return 0


@script.main
# zero arguments
async def _() -> None:
    print('called with no arguments')
    # returns None (which is treated as 0)


@script.main
# variable arguments
async def _(uuid: Optional[UUID], *args: rcu.Interface) -> None:
    if uuid is None:
        print('executed by non-player')
    else:
        players = await script.list_player()
        print(f'executed by player {players[uuid][0]}')
    print(f'called with {len(args)} argument(s): {", ".join(map(str, args))}')


rcu.run()
