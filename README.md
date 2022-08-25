## ❗ `dev` branch is under heavy development and is far from stable yet. Switch to `master` branch to receive a more stable (and with less capabilities) version of the mod.

# Redstone Computer Utilities

>  Lightweight and Modular Redstone Computer Debugging Tools. 

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## Features

(WIP)

## Installation

This mod supports Minecraft 1.19.1 and requires the latest Fabric Loader and Fabric API.

This mod is mostly server-side but has to be installed both on server-side and client-side in order to display translatable information correctly.

Python 3.6 or newer is required to use Python-related features.

## Usage

### Interfaces (not fully implemented yet)

Before any further operation with redstone signals, it is essential to abstract the input and output of your redstone mechanics into the form of interfaces. An interface is basically a row of special target blocks (or a sole target block if you want), which can be created either with the wand item and a command in the game or with a few lines of codes in scripts outside the game. By using the interfaces, redstone power can be received and emitted through these special target blocks, with the carried data being forwarded to scripts outside the game through the mod.

### Scripts (not fully implemented yet)

Scripts can read or write interfaces and response to specific events (e.g. gametick, data change, called from other scripts). See [docs/Scripts.md](./docs/Scripts.md) for details.

### Commands (not fully implemented yet)

All commands provided by the mod require at least permission level 2 (configurable). Specially, command `/rcu run` may require higher permission level, which is determined by the script. This means that command blocks may not be able to run it.

`/rcu`
- Give the command source player a wand item (configurable, pink dye as default).

`/rcu new <interface name> [option...]` (WIP)
- Create an interface. Name of an interface MUST be unique among all interfaces and MUST be a string only consists of letters, numbers and underlines.

`/rcu remove <interface name...>` (WIP)
- Remove the interface(s).

`/rcu info` (WIP)
- Display brief information and status.

`/rcu info interface` (WIP)
- Display information and status about interfaces.

`/rcu info interface <interface name...>` (WIP)
- Display the detail of the interface(s).

`/rcu info script` (WIP)
- Display information and status about scripts.

`/rcu info script <script name...>` (WIP)
- Display the detail of the script(s).

`/rcu run <script name> [argument...]` (WIP)
- Run the script with the arguments. Arguments may be interface names, script names or strings. Strings as arguments may be quoted to avoid ambiguity.

`/rcu reload` (WIP)
- Reload all scripts.

## Development

Java sources are located in `src/main/java/`. Python sources are located in `src/redstone_computer_utilities/`.

To build the mod, Java 17 or newer, Python 3.7.2 or newer and Poetry are required. Run the following command:

```sh
$ ./gradlew build
```

Built jars are located in `build/libs/`. Built wheels are located in `dist/`.

To extract translation keys, Java and GNU gettext are required. Run the following command:

```sh
$ ./gradlew extract
```

Extracted translation keys are located in `build/messages.json`. Translations are hosted on [transifex](https://www.transifex.com/nkid00/redstone-computer-utilities).

To convert translations into Minecraft-compatible json format, run the following command:

```sh
$ python po2minecraft.py path/to/messages.po path/to/messages.json
```

## Credits

- [Fabric Loader](https://github.com/FabricMC/fabric-loader), distributed under [Apache-2.0](https://github.com/FabricMC/fabric-loader/blob/master/LICENSE).

- [Fabric API](https://github.com/FabricMC/fabric), distributed under [Apache-2.0](https://github.com/FabricMC/fabric/blob/master/LICENSE).

- [GSON](https://github.com/google/gson), distributed under [Apache-2.0](https://github.com/google/gson/blob/master/LICENSE).

- [colorama](https://github.com/tartley/colorama), distributed under [BSD-3-Clause](https://github.com/tartley/colorama/blob/master/LICENSE.txt)

## Copyright

Copyright © 2021-2022 NKID00

Distributed under [MPL-2.0](./LICENSE).
