import redstone_computer_utilities as rcu


script = rcu.create_script('robust')


@script.main
async def _(n: str) -> None:
    if n == '0':
        print('#0 ResponseError without id')
        raise rcu.ResponseErrors.INVALID_AUTH_KEY
    elif n == '1':
        print('#1 ResponseError with id')
        await rcu.Interface('this_interface_does_not_exist', script).read()
    elif n == '2':
        print('#2 Exception')
        raise Exception('Some random error')
    elif n == '3':
        print('#3 Sudden exit')
        exit()
    else:
        raise rcu.ResponseErrors.ILLEGAL_ARGUMENT


rcu.run()
