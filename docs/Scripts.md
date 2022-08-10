# Scripts (not fully implemented yet)

Scripts are language-neutral programs outside the game that can read or write interfaces and are called when specific event occurs (e.g. gametick, data change, called from other scripts). For scripts written in Python, a library is provided to simplify the development. Name of a script MUST be unique among all registered scripts and MUST be a string consists of only letters, numbers and underlines.

## Communication

Communication between the mod and scripts is accomplished with two-way Json-RPC on a single TCP connection. Once initialized, the mod will bind host `localhost` (configurable), listen to port 37265 (configurable) and wait for connections from scripts. A single connection can be reused by multiple scripts with authorization keys as identifiers.

## Authorization

An authorization key will be given when the script is loaded and will be destroyed when the script is unloaded. Any further API call will require this key.

## Registration

When a script registers itself, it will not be runnable immediately. Instead, it will be queued and become loaded after `/rcu reload` is executed.

## API

See [api-openrpc.json](./api-openrpc.json) for details. Use experimental APIs with caution!

- Script
  - registerScript
  - deregisterScript
  - listScript
  - invokeScript
- Event Callback
  - registerCallback
  - deregisterCallback
  - *listCallback (experimental)*
  - *invokeCallback (experimental)*
- Interface
  - newInterface
  - removeInterface
  - listInterface
  - readInterface
  - writeInterface
- Logging
  - info
  - warn
  - error

## Event Callback

See [callback-openrpc.json](./callback-openrpc.json) for details. Use experimental event callbacks with caution!

- Script Lifecycle
  - onLoadScript
  - onUnloadScript
  - onRunScript
  - onInvokeScript
- Gametick
  - onGametickStart
  - onGametickEnd
- Interface
  - onDataChangeInterface
  - onReadInterface
  - onWriteInterface
  - *onNewInterface (experimental)*
  - *onRemoveInterface (experimental)*
