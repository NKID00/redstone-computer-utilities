# 外部程序（待开发）

外部程序是位于游戏外的编程语言无关的抽象程序，可以用来读写接口并在特定事件（如游戏刻、数据变化、被其他外部程序调用）发生时被调用。用 Python 编写外部程序时可以使用提供的库来简化开发。外部程序的名称**必须**在所有外部程序中唯一且**必须**只由字母、数字和下划线组成。

## 通信

模组与外部程序的通信使用一或多个 TCP 连接上的双向 JSON-RPC。模组初始化后会绑定环回地址（可配置），监听端口 37265（可配置）并等待外部程序的连接。单个连接可以被多个外部程序复用，此时由认证密钥充当标识符。每个外部程序**必须**只使用一个连接。当一个连接断开时，所有关联的外部程序都会被注销。

每个序列化的 json 消息在传输时都**必须**用开头的 2 字节大端序的长度字段封装为帧。序列化的 json 消息的长度**必须**小于或等于 65535。为避免冲突，发自模组的请求的 id **必须**为以 `s_` 开头的字符串，同时发自外部程序的请求的 id **必须**为以 `c_` 开头的字符串且**应该**为以 `c_<外部程序名>_` 开头的字符串。事件回调的 method 名称**应该**以 `<外部程序名>_` 开头。上述的 `<外部程序名>` 不包括尖括号。

由于 Minecraft 对多线程操作世界的限制，每个事件回调都会阻塞服务器主线程直到得到响应，得到来自正在处理该事件回调的外部程序的请求或超时，且模组只会在每个游戏刻开始时处理待处理的请求。外部程序**必须**利用 `onScriptRegister` 事件回调来保证服务器主线程在外部程序注册过程中被阻塞。

## 鉴权

认证密钥将会在外部程序注册时分发，并在外部程序注销或卸载时销毁。任何其他的 API 调用都需要这一认证密钥。

## 重新加载

当一个外部程序注册或注销时，依赖于这个外部程序的其他外部程序可能会出现异常，此时**应该**执行 `/rcu reload` 重新加载所有外部程序。执行 `/rcu reload` 时，所有外部程序的 `onScriptUnload` 事件回调会被调用，之后，所有外部程序的 `onScriptLoad` 事件回调会被调用。

## 稳定性

主版本号为零（0.x.x）的 API 和事件回调均不稳定，可能会在任何时候发生改变。主版本更新也可能带来破坏性改变。

## 区块卸载

对位于已卸载的区块中的接口的操作是未定义行为。

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
- 杂项
  - gametime
- 日志
  - info
  - warn
  - error

## 事件回调

详见 [callback-openrpc.json](./callback-openrpc.json)。谨慎使用实验性事件回调！

- 外部程序生命周期
  - onScriptRegister
  - onScriptReload
  - onScriptRun
  - onScriptInvoke
- 游戏刻
  - onGametickStart
  - onGametickEnd
  - onGametickStartDelay
  - onGametickEndDelay
  - onGametickStartClock
  - onGametickEndClock
- 接口
  - onInterfaceRedstoneUpdate
  - onInterfaceRead
  - onInterfaceWrite
  - *onInterfaceNew（实验性）*
  - *onInterfaceRemove（实验性）*
