# Python 外部程序支持库

## 导入并创建新外部程序

```python
import redstone_computer_utilities as rcu
script = rcu.create_script('my_script')
```

## 注册事件回调

```python
@script.on_gametick_start
async def _():  # 协程的名称不重要
    pass  # 在这里编写代码
```

必须用 async 关键字来声明协程。

## 调用 API

```python
await script.info('info from my script')
```

必须用 await 关键字来等待协程完成。
