# Details

## Interfaces

Interfaces can be utilized to abstract the input and output of your redstone mechanics to facilitate interaction with scripts. An interface is a row of target blocks (or a sole target block if you want), which can be created either with the wand item and a command or with scripts. By using the interfaces, redstone power can be received and emitted through these target blocks, with the carried data being forwarded to scripts outside the game through the mod.

## Scripts

Scripts can read or write interfaces or respond to specific events (e.g. gametick, data change). See [docs/Scripts.md](./Scripts.md).

## Commands

All commands provided by the mod require at least permission level 2 (configurable).

`/rcu`
- Give the command source player a wand item (configurable, pink dye as default).

`/rcu new <interface name> [option...]`
- Create an interface. Name of an interface MUST be unique among all interfaces and MUST be a string only consists of letters, numbers and underlines. Available Options are as follows。
  - `force` Do not check the integrity of the interface.
  - `skip=<number of blocks>` Skip specified number of target blocks between two bits.

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

`/rcu read <interface name...>`
- Read and display value of the interface.

`/rcu write <interface name> <value>`
- Write value into the interface. Values are case insensitive with binary prefix as `0b`, octal prefix as `0o` and hex prefix as `0x`.

`/rcu run <script name> [argument...]`
- Run the script with the arguments. Arguments may be interface name or any other string. Whether the argument is an interface will be inferred while quoted arguments are always considered string literal. Both of types and values of the arguments will be passed to the script.

`/rcu lang`
- Display current display language of the command source player.

`/rcu lang <language>`
- Set display language of the command source player.
