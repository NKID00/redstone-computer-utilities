import redstone_computer_utilities as rcu

script = rcu.create_script('nop_2gt_api_0')


@script.on_gametick_start_clock(rcu.gametick(2))
async def _():
    for _i in range(0):
        _gametime = await script.gametime()


rcu.run()
