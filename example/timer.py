import redstone_computer_utilities as rcu

script = rcu.create_script('timer')


@script.on_gametick_start_delay(rcu.redstonetick(4))
async def _():
    print('[on_gametick_start_delay] 4rt later')


@script.on_gametick_end_delay(rcu.redstonetick(4))
async def _():
    print('[on_gametick_end_delay] 4rt later')


@script.on_gametick_start_delay(rcu.gametick(12))
async def _():
    print('[on_gametick_start_delay] 12gt later')


@script.on_gametick_end_delay(rcu.gametick(12))
async def _():
    print('[on_gametick_end_delay] 12gt later')


@script.on_gametick_start_delay(rcu.second(1))
async def _():
    print('[on_gametick_start_delay] 1s later')


@script.on_gametick_end_delay(rcu.second(1))
async def _():
    print('[on_gametick_end_delay] 1s later')


# called at the start of the gametick for every 2 rt
@script.on_gametick_start_clock(rcu.redstonetick(2))
async def _():
    print(f'[on_gametick_start_clock] {await script.gametime()}')


# called at the end of the gametick for every 2 rt
@script.on_gametick_end_clock(rcu.redstonetick(2))
async def _():
    print(f'[on_gametick_end_clock] {await script.gametime()}')


rcu.run()
