import redstone_computer_utilities as rcu

script = rcu.create_script("gametime")


@script.on_gametick_start
async def _():
    gametime = script.gametime()
    print(f'Current gametime is: {gametime}')


rcu.run()
