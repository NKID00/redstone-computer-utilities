import redstone_computer_utilities as rcu


script = rcu.create_script('runtime_deregister')


async def callback():
    print(f'called at {await script.gametime()}')


@script.main
async def _(command: str):
    if command == 'register':
        # await is required to register event callbacks at runtime
        await script.on_gametick_start(callback)
        print('event callback added')
    elif command == 'deregister':
        await script.deregister_callback(callback)
        print('event callback removed')
    else:
        raise rcu.ResponseErrors.ILLEGAL_ARGUMENT


rcu.run()
