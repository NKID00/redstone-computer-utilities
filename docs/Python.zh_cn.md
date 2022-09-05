# Python 外部程序支持库

可用的事件回调和 API 参见位于 `example/` 的其他示例和 Script 类的公开成员和方法。

## 导入并创建新外部程序

```python
import redstone_computer_utilities as rcu
script = rcu.create_script('my_script')
```

## 注册事件回调

可以用函数装饰器来注册事件回调：（必须用 `async` 关键字来声明协程。）

```python
@script.on_gametick_start
async def _():  # 协程的名称不重要。
    print('Ticking!')  # 在这里编写代码。
```

也可以在运行时注册事件回调：（必须使用 `await` 关键字）

```python
async def update():
    print("I'm called!")

@script.main
async def _(interface: rcu.Interface):
    await script.on_interface_update(interface)(update)
```

## 调用 API

```python
await script.info('info from my script')
```

必须用 `await` 关键字来等待 API 调用完成。

## `main` 函数

当 `/rcu run` 被执行时 `main` 函数会被调用，`main` 函数只接受相同数量且与类型注解相同的参数。

例如，下面的 main 函数可以接受这条命令：

```
/rcu run my_script interface:my_interface
```

 而这条命令会被报参数无效错误：（由于参数类型不同，用引号括起会保证参数被视为字符串字面值）

```
/rcu run my_script "interface:my_interface"
```

这条命令也会：（由于参数数量太多）

```
/rcu run my_script interface:my_interface interface:interface42
```

```python
@script.main
async def _(my_interface_argument: rcu.Interface):
    # 参数保证已经被初始化但可能是无效的因为用户可能用不存在的名字作为参数。
    # 如果用户提供了一个不存在的接口名，接下来这行代码将会运行失败
    # 并抛出 ResponseError。
    print(await my_interface_argument.read())
```

可以使用变长定位参数来接受任意多个参数。多个 main 函数会依据类型注解分派运行。
