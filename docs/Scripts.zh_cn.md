# 外部程序（待开发）

外部程序是位于游戏外的编程语言无关的抽象程序，可以用来读写接口并在特定事件（如游戏刻、数据变化、被其他外部程序调用）发生时被调用。用 Python 编写外部程序时可以使用提供的库来简化开发。外部程序的名称**必须**在所有外部程序中唯一且**必须**只由字母、数字和下划线组成。

## 通信

模组与外部程序的通信使用一或多个 TCP 连接上的双向 Json-RPC。模组初始化后会绑定回环地址（可配置），监听端口 37265（可配置）并等待外部程序的连接。单个连接可以被多个外部程序复用，多个连接也可以被单个外部程序同时使用，此时由认证密钥充当标识符。每个 json 元素在传输时都**必须**在开头添加一个 2 字节大端序的长度字段。

## 鉴权

认证密钥将会在外部程序注册时分发，并在外部程序注销或卸载时销毁。任何其他的 API 调用都需要这一认证密钥。

## 注册

外部程序注册后并不可以立即被命令运行。相反地，该外部程序会被置入队列，在执行 `/rcu reload` 后才会被加载。

## 稳定性

主版本号为零（0.x.x）的 API 和事件回调均不稳定，可能会在任何时候发生改变。主版本更新也可能带来破坏性改变。

## API

详见 [api-openrpc.json](./api-openrpc.json)。谨慎使用实验性 API！

- 外部程序
  - registerScript
  - deregisterScript
  - listScript
  - invokeScript
- 事件回调
  - registerCallback
  - deregisterCallback
  - *listCallback（实验性）*
  - *invokeCallback（实验性）*
- 接口
  - newInterface
  - removeInterface
  - listInterface
  - readInterface
  - writeInterface
- 日志
  - info
  - warn
  - error

## 事件回调

详见 [callback-openrpc.json](./callback-openrpc.json)。谨慎使用实验性事件回调！

- 外部程序生命周期
  - onScriptLoad
  - onScriptUnload
  - onScriptRun
  - onScriptInvoke
- 游戏刻
  - onGametickStart
  - onGametickEnd
- 接口
  - onInterfaceRedstoneUpdate
  - onInterfaceRead
  - onInterfaceWrite
  - *onInterfaceNew（实验性）*
  - *onInterfaceRemove（实验性）*
