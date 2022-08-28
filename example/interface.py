import redstone_computer_utilities as rcu

script = rcu.create_script('interface')
interface1 = rcu.Interface('interface_test1', script)
interface2 = rcu.Interface('interface_test2', script)
number = 0


@script.on_gametick_start
async def _():
    global number
    await interface1.write(number)
    number += 1
    print(await interface2.read())


rcu.run()
