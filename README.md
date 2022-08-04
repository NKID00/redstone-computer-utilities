## ❗ `dev` branch is under heavy development and is far from stable yet. Switch to `master` branch to receive a more stable (and with less capabilities) version of the mod.

# Redstone Computer Utilities

> Various debug tools for redstone computers.

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## Features

(WIP)

<!-- - Use easy-to-debug files outside the game as RAMs for your redstone computers
- Adjustable bus sizes from 1 to 64 bits and shapes from horizontal, vertical ~~to even sloping lines~~(WIP)
- Different RAM types (read-only and write-only) as well as different clock types (positive, negative and dual edge triggering)
- Set-up instructions that are user-friendly
- Built-in English and 简体中文 (Simplified Chinese) translation -->

## Install

This mod supports Minecraft 1.19.1 and requires the latest Fabric Loader and Fabric API.

This mod is mostly server-side but has to be installed both on server-side and client-side in order to display translatable information correctly.

## Usage

### Components (not fully implemented yet)

This mod provides various types of components to interact with redstone signals and digital data. Some of the components exist in the game with the form of a row of target blocks and can be connected to redstone dust to receive or emit redstone power. The followings are the types of components:

- wires (WIP)
  - Unsynchronized input and output interfaces, transfer data in real time (actually, when a neighbor block update is triggered).
  - Can be connected to a wires.

- bus (WIP)
  - Synchronized input and output interfaces, transfer data only when clock signal arrives.
  - Can be connected to a bus.

- addrbus (WIP)
  - Synchronized and addressed (with another bus) input and output interfaces, transfer address and data simultaneously when clock signal arrives.
  - Can be connected to an addrbus.

- ram (WIP)
  - Read-write random access memories which stores data in actual game memory.
  - Can be connected from or to an addrbus.

- fileram (WIP)
  - Read-write random access memories which stores data in outside-game files.
  - Can be connected from or to an addrbus.

Components can be selected with the form of `<type>:<name>` where `<name>` is a case-sensitive string made of letters, digits and underlines. e.g. `addrbus:Input1` selects an addrbus named `Input1` and `fileram:new_executable` selects a fileram named `new_executable`. Components belonged to different types or different players can share the same name. Besides, it is possible but not recommended to name a component after the name of a component type.

### Connections (not fully implemented yet)

All the components can be connected with other components to transfer (whether synchronized or not) digital data unidirectionally from one end to another. The direction of the connection determines the direction of data flow.

It is required to use redstone dust to transfer data between components that cannot be connected in an obvious way to avoid possible ambiguities.

Connections between components can be selected with the form of `{source component selector}->{target component selector}`. e.g. `addrbus:input->ram:ram1` selects a connection from the addrbus named `input` to the ram named `ram1`.

### Commands (not fully implemented yet)

All the commands with abilities to perform operations on files (i.e. fileram-related, `/rcu newfile` and `/rcu removefile` etc.) require permission level 4 (because operations on files are dangerous). This means that command blocks cannot run these commands. Other commands require permission level 2.

`/rcu`
- Stop the running command if there is any, otherwise give the command source player a wand item (i.e. pink dye).

`/rcu new {component or connection selector} [option ...]` (WIP)
- Create a component or connection.

`/rcu remove {component or connection selector}` (WIP)
- Remove the component or connection.

`/rcu info` (WIP)
- Display brief information and status.

`/rcu info {component or connection selector}` (WIP)
- Display the detail of the component or connection.

`/rcu read {component selector} [option ...]` (WIP)
- Read from the component.

`/rcu write {component selector} [option ...] (<data> | from {component selector} [option ...])` (WIP)
- Write `<data>` or the result from reading from a component to the component.

`/rcu start {component or connection selector}` (WIP)
- Start the component or connection.

`/rcu stop {component or connection selector}` (WIP)
- Stop the component or connection.

`/rcu newfile <filename> <length in bytes>` (WIP)
- Create a file and fill it with `<length in bytes>` bytes of 0.

`/rcu removefile <filename>` (WIP)
- Remove the file.

`/rcu asm <instruction set architecture> [option ...] <assembly code> (to {component selector} [option ...])` (WIP)
- Assemble the given assembly code and write the result to the component.

`/rcu disasm <instruction set architecture> [option ...] (<machine code> | from {component selector} [option ...])` (WIP)
- Disassemble the given machine code or the result from reading from a component.

## Build

```sh
./gradlew build
```

Built jars are located in `build/libs`.

## Copyright

Copyright © 2021-2022 NKID00

Licensed under [MPL-2.0](./LICENSE).
