import redstone_computer_utilities as rcu

script = rcu.create_script('interface_wait')
interface1 = rcu.Interface('interface1', script)
interface2 = rcu.Interface('interface2', script)


@script.on_interface_update_immediate(interface1)
async def _():
    await interface2.write(await interface1.read())


rcu.run()
