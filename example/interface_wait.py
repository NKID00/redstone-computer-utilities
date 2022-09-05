import redstone_computer_utilities as rcu

script = rcu.create_script('interface_wait')
interface1 = rcu.Interface('interface1', script)
interface2 = rcu.Interface('interface2', script)


@script.on_interface_update(interface2)
async def _():
    value = await interface2.read()
    await script.wait(rcu.second(2))
    value += 1
    await interface1.write(value)


rcu.run()
