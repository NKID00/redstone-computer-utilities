## ❗ `dev` branch is under heavy development and is far from stable yet. Switch to `master` branch to receive a more stable (and with less capabilities) version of the mod.

# Redstone Computer Utilities

>  Lightweight and Modular Redstone Computer Debugging Tools. 

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## Features

- Empowers programs in any programming language (as long as it supports JSON and TCP) to debug redstone computers
  - Specially, a Python library is provided to simplify development
- Supports redstone wires in any sizes and shapes from horizontal, vertical to even diagonal ones
- Built-in English and Simplified Chinese translations
- Server-side-only implementation, fully compatible with vanilla clients
- Compatible with tick speed controlling, stepping and pausing implemented by other mods

## Installation

This mod supports Minecraft 1.19.2 and requires the latest Fabric Loader and Fabric API.

Only server-side installation is required for multiplayer and client-side installation is required for singleplayer.

Python 3.6 or newer is required to use the provided Python library.

## Basic Usage

1. Execute `/rcu` to receive a wand item (or get a pink dye by yourself).
2. Attach target blocks to redstone wires of your redstone mechanics to be debugged.
3. Left click with the wand item to select the most significant bit, right click to select the least significant bit.
4. Execute `/rcu new <interface name>` to create a interface.
5. Write debugging program and wrap it as a script.
6. Execute `/rcu run <script name> <interface name>` to run the script.

You may also accelerate tick speed if it takes too long.

## Details

### Interfaces

Before any further operation with redstone signals, it is essential to abstract the input and output of your redstone mechanics into the form of interfaces. An interface is basically a row of special target blocks (or a sole target block if you want), which can be created either with the wand item and a command in the game or with a few lines of codes in scripts outside the game. By using the interfaces, redstone power can be received and emitted through these special target blocks, with the carried data being forwarded to scripts outside the game through the mod.

### Scripts (not fully implemented yet)

Scripts can read or write interfaces and response to specific events (e.g. gametick, data change, called from other scripts). See [docs/Scripts.md](./docs/Scripts.md) for details.

### Commands (not fully implemented yet)

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

Extracted translation keys are located in `build/messages.po`. Translations are hosted on [transifex](https://www.transifex.com/nkid00/redstone-computer-utilities).

To convert translations to or from Minecraft-compatible json format, run the following command:

```sh
$ python po2minecraft.py path/to/messages.po path/to/messages.json
$ python minecraft2po.py path/to/messages.json path/to/messages.po
```

## Credits

- [Fabric Loader](https://github.com/FabricMC/fabric-loader), distributed under [Apache-2.0](https://github.com/FabricMC/fabric-loader/blob/master/LICENSE).
- [Fabric API](https://github.com/FabricMC/fabric), distributed under [Apache-2.0](https://github.com/FabricMC/fabric/blob/master/LICENSE).
- [GSON](https://github.com/google/gson), distributed under [Apache-2.0](https://github.com/google/gson/blob/master/LICENSE).
- [colorama](https://github.com/tartley/colorama), distributed under [BSD-3-Clause](https://github.com/tartley/colorama/blob/master/LICENSE.txt).

## Copyright

Copyright © 2021-2022 NKID00

Distributed under [MPL-2.0](./LICENSE).
