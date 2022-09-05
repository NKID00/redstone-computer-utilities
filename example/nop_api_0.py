import redstone_computer_utilities as rcu

script = rcu.create_script('nop_api_0')


@script.on_gametick_start
async def _():
    for _i in range(0):
        _gametime = await script.gametime()


rcu.run()
