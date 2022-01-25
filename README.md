# Redstone Computer Utilities

> Simple debug tools for redstone computers.

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## Features

<!-- - Use easy-to-debug files outside the game as RAMs for your redstone computers
- Adjustable bus sizes from 1 to 64 bits and shapes from horizontal, vertical ~~to even sloping lines~~(WIP)
- Different RAM types (read-only and write-only) as well as different clock types (positive, negative and dual edge triggering)
- Set-up instructions that are user-friendly
- Built-in English and 简体中文 (Simplified Chinese) translation -->

## Install

This mod supports Minecraft 1.18.1 and requires the latest Fabric Loader & Fabric API. This mod is mostly server-side but has to be installed both on server-side and client-side in order to display translatable information correctly.

## Usage

### Components (not fully implemented yet)

Components can be connected with other components and transfer (whether synchronized or not) digital redstone signal from one end to another. The direction of the connection determines the direction of data flow. The followings are the types of components:

- wires (WIP)
  - Unsynchronized (i.e. real-time) input and output interfaces.
  - Can be connected with a wires.

- bus (WIP)
  - Synchronized (i.e. with a clock signal) input and output interfaces.
  - Can be connected with a bus.

- addrbus (WIP)
  - Synchronized and addressed (with another bus) input and output interfaces.
  - Can be connected with an addrbus.

- ram (WIP)
  - Read-write random access memories which stores data in actual game memory.
  - Can be connected with an addrbus.

- fileram (WIP)
  - Read-write random access memories which stores data in outside-game files.
  - Can be connected with an addrbus.

Connections are WIP.

Components can be selected by `<type>:<name>` where name is a case-sensitive string made of letters, numbers and underlines, e.g. `addrbus:Input1` or `fileram:new_executable`. It is possible but not recommended to name a component after the name of a component type. Connections between components can be selected by `{source component selector}->{target component selector}`, e.g. `addrbus:input->ram:ram1` or `wires:source1->wires:target1`.

### Commands (not fully implemented yet)

All the commands with abilities to perform operations on files (i.e. fileram-related, `/rcu newfile` and `/rcu removefile` etc.) require permission level 4 or higher (because operations on files are dangerous). This means that command blocks cannot run these commands. Other commands require permission level 2 or higher.

`/rcu` (WIP)
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

`/rcu asm <instruction set architecture> [option ...] <assembly code>` (WIP)
- Assemble the given assembly code.

`/rcu disasm <instruction set architecture> [option ...] (<machine code> | from {component selector} [option ...])` (WIP)
- Disassemble the given machine code or the result from reading from a component.

## Copyright

Copyright © 2021-2022 NKID00

Licensed under [the MIT license](./LICENSE).
