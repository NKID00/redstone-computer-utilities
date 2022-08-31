# Scripts (not fully implemented yet)

Scripts are language-neutral programs outside the game that can read or write interfaces and are called when specific event occurs (e.g. gametick, data change, called from other scripts). For scripts written in Python, a library is provided to simplify the development. Name of a script MUST be unique among all registered scripts and MUST be a string consists of only letters, numbers and underlines.

## Communication

Communication between the mod and scripts is accomplished with two-way JSON-RPC on one or more TCP connections. Once initialized, the mod will bind the loopback address (configurable), listen to port 37265 (configurable) and wait for connections from scripts. A single connection may be reused by multiple scripts, with authorization keys as identifiers. Each script MUST use only one connection. When a connection is lost, all scripts related to the connection will be deregistered.

Each serialized json message MUST be framed with a 2-byte big-endian length field prepended while transferring. Length of the serialized json message MUST be less than or equal to 65535. To avoid collision, ids of requests sent by the mod MUST be a string started with `s_`, while ids of requests sent by scripts MUST be a string started with `c_` and SHOULD be a string started with `c_<script name>_`. Names of event callback methods SHOULD start with `<script name>_`. The above `<script name>` excludes the angle brackets.

Due to restrictions of multithread world operation in Minecraft, every event callback will block the main thread of the server until a response arrives, a request sent by script handling the event callback arrives or time is out, and the mod will handle pending requests only at the start of every gametick. Scripts MUST utilize the event callback `onScriptRegister` to make sure that the main thread of the server is blocked during script registration process.

## Authorization

An authorization key will be given when the script is registered and will be destroyed when the script is deregistered. Any further API call will require this key.

## Reload

When a script is registered or deregistered, other scripts that depends on it may malfunction and `/rcu reload` SHOULD be executed to reload all scripts. When `/rcu reload` is executed, `onScriptReload` event callback will be called on all scripts.

## Stability

Major version zero (0.x.x) is not considered stable, API and event callbacks may change at any time. Breaking changes may also occur when major version changes.

## Chunk Unloading

Manipulating interfaces located in unloaded chunks is undefined behavior.

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
  - infoInterface
  - readInterface
  - writeInterface
- Miscellaneous
  - gametime
  - *listPlayer (experimental)*
- Logging
  - info
  - warn
  - error
  - sendInfo
  - sendWarn
  - sendError

## Event Callback

See [callback-openrpc.json](./callback-openrpc.json) for details. Use experimental event callbacks with caution!

- Script Lifecycle
  - onScriptRegister
  - onScriptReload
  - onScriptRun
  - onScriptInvoke
- Gametick
  - onGametickStart
  - onGametickEnd
  - onGametickStartDelay
  - onGametickEndDelay
  - onGametickStartClock
  - onGametickEndClock
- Interface
  - onInterfaceRedstoneUpdate
  - onInterfaceRead
  - onInterfaceWrite
  - *onInterfaceNew (experimental)*
  - *onInterfaceRemove (experimental)*
