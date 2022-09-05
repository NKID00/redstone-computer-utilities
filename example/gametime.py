import redstone_computer_utilities as rcu

script = rcu.create_script('gametime')


@script.on_gametick_start
async def _():
    gametime = await script.gametime()
    print(f'[on_gametick_start] Current gametime is: {gametime}')
    await script.info(f'[on_gametick_start] Current gametime is: {gametime}')


@script.on_gametick_end
async def _():
    gametime = await script.gametime()
    print(f'[on_gametick_end] Current gametime is: {gametime}')
    await script.info(f'[on_gametick_end] Current gametime is: {gametime}')


rcu.run()
