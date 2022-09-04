# Python Library for Scripts

See also other examples located in `example/` and public members and methods of class Script for available event callbacks and APIs.

## Import and create a new script
```python
import redstone_computer_utilities as rcu
script = rcu.create_script('my_script')
```

## Register a event callback

```python
@script.on_gametick_start
async def _():  # Name of the coroutine is insignificant.
    print("I'm called!")  # Write real code here.
```

Keyword `async` is required to define a coroutine.

## Call API

```python
await script.info('info from my script')
```

Keyword `await` is required to wait the coroutine to finish.

## `main` function

`main` function is called when `/rcu run` is executed and only accepts arguments of same amount and same types as type annotations.


For instance, this command is acceptable for the following main function:

```
/rcu run my_script interface:my_interface
```

However, this command responds with invalid argument error: (due to incompatible type, quoted strings are guaranteed to be considered literal)

```
/rcu run my_script "interface:my_interface"
```

and also this: (due to too many arguments)

```
/rcu run my_script interface:my_interface interface:interface42
```

```python
@script.main
async def _(my_interface_argument: rcu.Interface):
    # Arguments are guaranteed to be initialized but may be invalid as users
    # may choose names that does not exist as arguments.
    # If interface with the name from the argument does not exists, the
    # following line of code fails and a ResponseError is raised.
    print(await my_interface_argument.read())
```

Variable positional argument may be utilized to accept any number of arguments. Multiple `main` functions will be dispatched and called according to their type annotations.
