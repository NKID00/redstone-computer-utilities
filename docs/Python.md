# Python Library for Scripts

## Import and create a new script
```python
import redstone_computer_utilities as rcu
script = rcu.create_script('my_script')
```

## Register a event callback

```python
@script.on_gametick_start
async def _():  # name of the coroutine is insignificant
    pass  # write real code at here
```

Keyword `async` is required to define a coroutine.

## Call API

```python
await script.info('info from my script')
```

Keyword `await` is required to wait the coroutine to finish.
