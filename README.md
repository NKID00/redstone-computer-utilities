## ❗ `dev` branch is under heavy development and is far from stable yet. Switch to `master` branch to receive a more stable (and with less capabilities) version of the mod.

# Redstone Computer Utilities

> Various debug tools for redstone computers.

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

Scripts can read or write interfaces. Currently scripts can only be written in Python.

### Commands (not fully implemented yet)

Command `/rcu run` requires permission level 2 or higher, which is determined by the script. This means that command blocks may not be able run it. Other commands require permission level 2.

`/rcu` (WIP)
- Give the command source player a wand item (configurable, pink dye as default).

`/rcu new <interface name> [option...]` (WIP)
- Create a interface.

`/rcu remove <interface name>` (WIP)
- Remove the interface.

`/rcu info` (WIP)
- Display brief information and status.

`/rcu info <interface name>` (WIP)
- Display the detail of the interface.

`/rcu run <script name> [argument...]` (WIP)
- Run the script with the arguments.

## Development

Java sources are located in `src/main/java/`. Python sources are located in `src/redstone_computer_utilities/`.

To build the mod, Java 17 or newer, Python 3.7.2 or newer and the latest Poetry are required. Run the following commands:

```sh
$ ./gradlew build
$ poetry build
```

Built jars are located in `build/libs/`. Built wheels are located in `dist/`.

## Credits

[Py4J](https://www.py4j.org/), distributed under [BSD-3-Clause](https://github.com/py4j/py4j/blob/master/LICENSE.txt).

## Copyright

Copyright © 2021-2022 NKID00

Distributed under [MPL-2.0](./LICENSE).
