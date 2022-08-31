# Details

## Interfaces

Before any further operation with redstone signals, it is essential to abstract the input and output of your redstone mechanics into the form of interfaces. An interface is basically a row of special target blocks (or a sole target block if you want), which can be created either with the wand item and a command in the game or with a few lines of codes in scripts outside the game. By using the interfaces, redstone power can be received and emitted through these special target blocks, with the carried data being forwarded to scripts outside the game through the mod.

## Scripts

Scripts can read or write interfaces and response to specific events (e.g. gametick, data change, called from other scripts).

See [docs/Scripts.md](./docs/Scripts.md) for documentation about scripts.

## Commands

All commands provided by the mod require at least permission level 2 (configurable). Specially, command `/rcu run` may require higher permission level, which is determined by the script. This means that command blocks may not be able to run it. Source of any command may be changed with `/execute as`.

`/rcu`
- Give the command source player a wand item (configurable, pink dye as default).

`/rcu new <interface name> [option...]`
- Create an interface. Name of an interface MUST be unique among all interfaces and MUST be a string only consists of letters, numbers and underlines.

`/rcu remove <interface name...>`
- Remove the interface(s).

`/rcu info`
- Display information about interfaces and scripts.

`/rcu info interface`
- Display information about interfaces.

`/rcu info interface <interface name...>`
- Display detailed information of the interface(s).

`/rcu info script`
- Display information about scripts.

`/rcu info script <script name...>`
- Display detailed information of the script(s).

`/rcu run <script name> [argument...]` (WIP)
- Run the script with the arguments. Arguments may be interface names, script names or any other strings.

`/rcu reload` (WIP)
- Reload all scripts.

`/rcu lang`
- Display current display language of the command source player.

`/rcu lang <language>`
- Set display language of the command source player.

## Performance

When tested on Minecraft 1.19.2 server on Kubuntu `22.04.1 LTS x86_64` (Kernel `5.15.0-46-lowlatency`) on CPU `AMD Ryzen 7 4800U`, A no-op event callback consumes ~0.27ms and a no-op API call consumes ~0.12ms. Raw data is as follows:

|                                                             | tps  | mspt |
| ----------------------------------------------------------- | ---- | ---- |
| Mod not installed                                           | 1382 | 0.72 |
| No script registered                                        | 1383 | 0.72 |
| onGametickStart event callback, no API calls                | 1013 | 0.99 |
| onGametickStart event callback, 1 API call for each event   | 929  | 1.08 |
| onGametickStart event callback, 10 API calls for each event | 458  | 2.18 |

Measured using carpet mod with command `/tick warp 100000` and scripts `example/nop_api_*.py`, no player is logged in.
