import redstone_computer_utilities as rcu


script = rcu.create_script('runtime_event_callback')


def update(interface):
    # closure is used to hold the targeted interface
    async def inner():
        print(f'value of {interface} is updated to {await interface.read()}')
    return inner


@script.main
async def _(interface: rcu.Interface):
    await script.on_interface_update(interface)(update(interface))
    print(f'event callback of {interface} added')


rcu.run()
