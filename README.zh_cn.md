# Redstone Computer Utilities

> 一些简单的红石计算机调试工具。 **\[尚未完工\]**

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## 特性

- 使用易于调试的游戏外文件充当红石计算机的内存
- 可使用 1 到 64 位之间各种宽度的水平、竖直甚至斜着的各种形状的总线
- 不同的内存类型（只读和只写）以及不同的时钟（上升沿、下降沿和双边沿触发）
- 对用户友好的设置步骤提示
- 内置简体中文和英文翻译

## 安装

此模组支持 Minecraft 1.16.5，需要最新的 Fabric Loader 和 Fabric API。

此模组大部分是服务器侧的，但必须同时在服务器和客户端安装才能正确显示翻译文本。

## 用法

下列所有命令均需要权限等级 4 或以上（因为文件操作较危险），这意味着命令方块将无法执行这些命令。

- `/rcu`
  - 如果有正在运行的命令则停止它，否则给予执行者一个辅助物品（粉红色染料）。

- `/rcu fileram`
  - 与 `/rcu fileram info` 相同。

- `/rcu fileram info [<name>]`
  - 如果不指定 `<name>` 则列出所有的文件内存信息，否则显示 `<name>` 指定的文件内存信息。

- `/rcu fileram new <type> <clock triggering edge> <name> <file> [<endianness>]`
  - 创建一个新的名为 `<name>` 的类型为 `<type>` (`ro` 代表只读, `wo` 代表只读）且时钟类型为 `<clock triggering edge>`（`pos` 代表上升沿触发, `neg` 代表下降沿触发，`dual` 代表双边沿触发）的文件内存并将其和名为 `<file>`* 的文件关联（~~使用字节序 `<endianness>`~~ 字节序控制尚未完工）。运行这条指令后，屏幕上会显示一些提示步骤来引导你进行下一步操作。

- `/rcu fileram remove <name>`
  - 移除名为 `<name>` 的文件内存。

- `/rcu fileram start <name>`
  - 开始运行名为 `<name>` 的文件内存。

- `/rcu fileram stop <name>`
  - 停止运行名为 `<name>` 的文件内存。

- `/rcu fileram newfile <file> <length in bytes>`
  - 创建名为 `<file>`* 的文件并使用 `<length in bytes>` 字节的 0 填充。

- `/rcu fileram removefile <file>`
  - 移除名为 `<file>`* 的文件。

*文件内存对应的文件存储于目录 `rcutil/fileram/` 下（在单人模式下是 `.minecraft/rcutil/fileram/`）。

提示：文件内存相关配置仅存储于内存中，服务器重启后会被移除（在单人模式下客户端重启后会被移除）。

## 版权

版权所有 © 2021 NKID00

使用 [MIT 许可证](./LICENSE)进行许可。
