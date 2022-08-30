## ❗ `dev` 分支正在开发中，极不稳定。`master` 分支是更稳定（但功能更少）的版本。

<img src="./src/main/resources/assets/rcutil/icon.png" alt="icon" align="right" height="175">

# Redstone Computer Utilities

>  轻量级模块化红石计算机调试工具。

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## 特性

- 可以用任何编程语言（只要它支持 JSON 和 TCP）编写的程序来调试红石计算机
  - 特别地，用 Python 编写外部程序时可以使用提供的库来简化开发
- 支持各种位宽和水平、竖直甚至倾斜的各种形状的红石排线
- 内置简体中文和英文翻译（执行 `/rcu lang zh_cn` 来设置显示语言为简体中文）
- 纯服务端实现，完全兼容原版客户端
- 兼容其他模组实现的游戏刻调速，步进和暂停

## 安装

支持 Minecraft 1.19.2，需要安装最新的 Fabric Loader 和 Fabric API。

多人游戏只需要安装在服务器侧，单人游戏需要安装在客户端侧。

需要安装 Python 3.6 或更新版本来使用提供的 Python 库。

## 基础用法

1. 执行 `/rcu` 来获取一个魔杖物品（也可以直接拿一个粉红色染料）。
2. 将需要调试的红石排线连接上标靶方块。
3. 使用魔杖物品左键选择最高有效位，右键选择最低有效位。
4. 执行 `/rcu new <接口名>` 来创建一个接口。
5. 编写调试程序并将其封装为外部程序。
6. 执行 `/rcu run <外部程序名> <接口名>` 来运行调试程序。

如果调试程序耗时太久，也可以尝试加快游戏刻速度。

## 细节

### 接口

在与红石信号交互之前必须要将红石输入输出抽象为接口。接口是一排特殊的标靶方块（或者一个标靶方块也行），可以通过魔杖物品和指令在游戏内创建或通过代码在游戏外创建。接口可以用这些特殊标靶方块来接受或发出红石信号，同时将相应的数据通过模组传递给外部程序。

### 外部程序（待开发）

外部程序可以用来读写接口并响应事件（如游戏刻、数据变化、被其他外部程序调用）。详见 [docs/Scripts.zh_cn.md](./docs/Scripts.zh_cn.md)。

### 命令（待开发）

所有由模组提供的命令的权限等级都至少为 2（可配置）。特殊地，命令 `/rcu run` 的权限等级可能更高，由外部程序决定。这意味着命令方块可能无法运行它。所有命令都可以使用 `/execute as` 来改变执行者身份。

`/rcu`
- 给予命令执行者一个魔杖物品（可配置，默认为粉红色染料）。

`/rcu new <接口名> [选项...]`
- 创建接口。接口名**必须**在所有接口中唯一且**必须**只由字母、数字和下划线组成。

`/rcu remove <接口名...>`
- 删除接口。

`/rcu info`
- 显示接口和外部程序信息。

`/rcu info interface`
- 显示接口信息。

`/rcu info interface <接口名...>`
- 显示指定接口详情。

`/rcu info script`
- 显示外部程序信息。

`/rcu info script <外部程序名...>`
- 显示指定外部程序详情。

`/rcu run <外部程序名> [参数...]`（待开发）
- 使用指定的参数运行外部程序。参数可以是接口名、外部程序名或其他任意字符串。

`/rcu reload`（待开发）
- 重新加载所有外部程序。

`/rcu lang`
- 显示命令执行者的当前显示语言。

`/rcu lang <语言>`
- 设置命令执行者的显示语言。

### 性能

在 `AMD Ryzen 7 4800U` CPU 上运行的 Kubuntu `22.04.1 LTS x86_64`（Kernel `5.15.0-46-lowlatency`）上运行的 Minecraft 1.19.2 服务器上测试时，一次空事件回调消耗约 0.27ms，一次空 API 调用消耗约 0.12ms。原始测试结果如下：

|                                                             | tps  | mspt |
| ----------------------------------------------------------- | ---- | ---- |
| 未安装模组                                                  | 1382 | 0.72 |
| 无外部程序                                                  | 1383 | 0.72 |
| onGametickStart 事件回调，无 API 调用                       | 1013 | 0.99 |
| onGametickStart 事件回调，每次事件回调时进行 1 次 API 调用  | 929  | 1.08 |
| onGametickStart 事件回调，每次事件回调时进行 10 次 API 调用 | 458  | 2.18 |

测试使用 carpet 模组命令 `/tick warp 100000` 和外部程序 `example/nop_api_*.py`，无玩家登录。

## 开发

Java 源代码文件位于 `src/main/java/`。Python 源代码文件位于 `src/redstone_computer_utilities/`。

要构建模组，需要安装 Java 17 或更新版本，Python 3.7.2 或更新版本和 Poetry。运行以下命令：

```sh
$ ./gradlew build
```

构建出的 jar 文件位于 `build/libs/`。构建出的 wheel 文件位于 `dist/`。

要提取翻译键，需要安装 Java 和 GNU gettext。运行以下命令：

```sh
$ ./gradlew extract
```

提取的翻译键位于 `build/messages.po`。翻译使用 [transifex](https://www.transifex.com/nkid00/redstone-computer-utilities) 托管。

要将翻译转换为 Minecraft 兼容的格式或从 Minecraft 兼容的格式转换，运行以下命令：

```sh
$ python po2minecraft.py 文件路径/messages.po 文件路径/messages.json
$ python minecraft2po.py 文件路径/messages.json 文件路径/messages.po
```

## 鸣谢

- [Fabric Loader](https://github.com/FabricMC/fabric-loader)，使用 [Apache-2.0](https://github.com/FabricMC/fabric-loader/blob/master/LICENSE) 许可证分发。
- [Fabric API](https://github.com/FabricMC/fabric)，使用 [Apache-2.0](https://github.com/FabricMC/fabric/blob/master/LICENSE) 许可证分发。
- [GSON](https://github.com/google/gson)，使用 [Apache-2.0](https://github.com/google/gson/blob/master/LICENSE) 许可证分发。
- [colorama](https://github.com/tartley/colorama)，使用 [BSD-3-Clause](https://github.com/tartley/colorama/blob/master/LICENSE.txt) 许可证分发。

## 版权

版权所有 © 2021-2022 NKID00

使用 [MPL-2.0](./LICENSE) 许可证分发。
