import redstone_computer_utilities as rcu

script = rcu.create_script('nop_api_1')


@script.on_gametick_start
async def _():
    for _i in range(1):
        _gametime = await script.gametime()


rcu.run()
