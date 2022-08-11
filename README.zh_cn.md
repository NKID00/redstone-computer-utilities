## ❗ `dev` 分支正在开发中，极不稳定。`master` 分支是更稳定（但功能更少）的版本。

# Redstone Computer Utilities

>  轻量级模块化红石计算机调试工具。

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## 特性

（待补充）

## 安装

支持 Minecraft 1.19.1，需要安装最新的 Fabric Loader 和 Fabric API。

这个模组主要工作在服务器侧，但必须同时安装在服务器侧和客户端侧来正确显示翻译。

需要安装 Python 3.6 或更新版本来使用 Python 相关功能。

## 用法

### 接口（待开发）

在与红石信号交互之前必须要将红石输入输出抽象为接口。接口是一排特殊的标靶方块（或者一个标靶方块也行），可以通过魔杖物品和指令在游戏内创建或通过代码在游戏外创建。接口可以用这些特殊标靶方块来接受或发出红石信号，同时将相应的数据通过模组传递给外部程序。

### 外部程序（待开发）

外部程序可以用来读写接口并响应事件（如游戏刻、数据变化、被其他外部程序调用）。详见 [docs/Scripts.zh_cn.md](./docs/Scripts.zh_cn.md)。

### 命令（待开发）

所有由模组提供的命令的权限等级都至少为 2（可配置）。特殊地，命令 `/rcu run` 的权限等级可能更高，由外部程序决定。这意味着命令方块可能无法运行它。

`/rcu`
- 给予命令执行者一个魔杖物品（可配置，默认为粉红色染料）。

`/rcu new <接口名> [选项...]`（待开发）
- 创建接口。接口名必须在所有接口中唯一且只能由字母、数字和下划线组成。

`/rcu remove <接口名...>`（待开发）
- 删除接口。

`/rcu info`（待开发）
- 显示简略状态信息。

`/rcu info interface`（待开发）
- 显示接口状态信息。

`/rcu info interface <接口名...>`（待开发）
- 显示指定接口详情。

`/rcu info script`（待开发）
- 显示外部程序状态信息。

`/rcu info script <外部程序名...>`（待开发）
- 显示指定外部程序详情。

`/rcu run <外部程序名> [参数...]`（待开发）
- 使用指定的参数运行外部程序。

`/rcu reload`（待开发）
- 重新加载所有外部程序。

## 开发

Java 源代码文件位于 `src/main/java/`。Python 源代码文件位于 `src/redstone_computer_utilities/`。

要构建模组，需要安装 Java 17 或更新版本，Python 3.7.2 或更新版本和最新的 Poetry。运行以下命令：

```sh
$ ./gradlew build
$ poetry build
```

构建出的 jar 文件位于 `build/libs/`。构建出的 wheel 文件位于 `dist/`。

## 鸣谢

- [Fabric Loader](https://github.com/FabricMC/fabric-loader)，使用 [Apache-2.0](https://github.com/FabricMC/fabric-loader/blob/master/LICENSE) 许可证分发。

- [Fabric API](https://github.com/FabricMC/fabric)，使用 [Apache-2.0](https://github.com/FabricMC/fabric/blob/master/LICENSE) 许可证分发。

- [GSON](https://github.com/google/gson)，使用 [Apache-2.0](https://github.com/google/gson/blob/master/LICENSE) 许可证分发。

## 版权

版权所有 © 2021-2022 NKID00

使用 [MPL-2.0](./LICENSE) 许可证分发。
