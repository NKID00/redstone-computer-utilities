import redstone_computer_utilities as rcu

script = rcu.create_script('gametime')


@script.on_gametick_start
async def _():
    gametime = await script.gametime()
    if gametime % 20 == 0:
        print(f'Current gametime is: {gametime}')
        await script.info(f'Current gametime is: {gametime}')


rcu.run()
