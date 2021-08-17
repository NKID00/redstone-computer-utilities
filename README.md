# Redstone Computer Utilities

> Simple debug tools for redstone computers. **\[WIP\]**

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## Features

- Use easy-to-debug files outside the game as RAMs for your redstone computers
- Adjustable bus sizes from 1 to 64 bits and shapes from horizontal, vertical to even sloping lines
- Different RAM types (read-only and write-only) as well as different clock types (positive, negative and dual edge triggering)
- Set-up instructions that are user-friendly
- Built-in English and 简体中文 (Simplified Chinese) translation

## Install

This mod supports Minecraft 1.16.5 and relies on the latest Fabric Loader & Fabric API.

This mod is mostly a server-side mod but has to be installed both on server-side and client-side in order to display translations correctly.

## Usage

All the commands listed here requires permission level 4 or higher (because operations on files are dangerous), which means command blocks cannot run these commands.

- `/rcu`
  - Stop running command if there is any, otherwise give command source player a wand item (pink dye).

- `/rcu fileram`
  - Same as `/rcu fileram info`.

- `/rcu fileram info [<name>]`
  - List all file RAMs if `<name>` is not specified, otherwise display the detail of the file RAM named `<name>`.

- `/rcu fileram new <type> <clock triggering edge> <name> <file> [<endianness>]`
  - Create a new file RAM named `<name>` with type `<type>` (`ro` for read-only, `wo` for write-only), clock triggering edge `<clock triggering edge>` (`pos` for positive edge triggering, `neg` for negative edge triggering, `dual` for dual edge triggering) and connect it with the file name `<file>`* (~~with endianness `<endianness>`~~ endianness control is still WIP). After running this command, several instructions displayed on the screen will tell you what to do next.

- `/rcu fileram remove <name>`
  - Remove the file RAM named `<name>`.

- `/rcu fileram start <name>`
  - Start running the file RAM named `<name>`.

- `/rcu fileram stop <name>`
  - Stop running the file RAM named `<name>`.

- `/rcu fileram newfile <file> <length in bytes>`
  - Create a file named `<file>`* and fill it with `<length in bytes>` bytes of 0.

- `/rcu fileram removefile <file>`
  - Remove the file named `<file>`*.

*Files of file RAMs are stored in the directory `rcutil/fileram/` (or `.minecraft/rcutil/fileram/` when playing single player mode).

Note: File RAMs configurations only stores in memory and will be removed after server restart (or client restart when playing single player mode).

## Copyright

Copyright © 2021 NKID00

Licensed under [the MIT license](./LICENSE).
