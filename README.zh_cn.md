<img src="./src/main/resources/assets/rcutil/icon.png" alt="icon" align="right" height="175">

# Redstone Computer Utilities

>  轻量级模块化红石计算机调试工具。

[English README](./README.md) | [简体中文简介](./README.zh_cn.md)

## 亮点

- 可以用任何编程语言（只要它支持 JSON 和 TCP）编写的程序来调试红石计算机
  - 特别地，可以使用提供的 [Python 库](https://github.com/NKID00/redstone-computer-utilities-python)来简化开发
- 支持各种位宽和水平、竖直甚至倾斜的各种形状的红石排线
- 全部命令的所有参数都拥有自动补全和提示
- 纯服务端实现，完全兼容原版客户端
- 兼容其他模组实现的游戏刻调速，步进和暂停
- 内置简体中文和英文翻译（执行 `/rcu lang zh_cn` 来设置显示语言为简体中文）

## 安装

支持 Minecraft 1.16-1.19.3，需要安装 Java 17、最新的 Fabric Loader 和最新的 Fabric API。

多人游戏只需要安装在服务器侧，单人游戏需要安装在客户端侧。

## 基础用法

1. 执行 `/rcu` 来获取一个魔杖物品（也可以直接拿一个粉红色染料）。
2. 将需要调试的红石排线连接上标靶方块。
3. 使用魔杖物品左键选择最高有效位，右键选择最低有效位。
4. 执行 `/rcu new <接口名>` 来创建一个接口。
5. 编写调试程序并将其封装为外部程序。
6. 运行外部程序。
7. 执行 `/rcu run <外部程序名> <接口名>` 来运行调试程序。

如果调试程序耗时太久也可以尝试加快游戏刻速度。

详见 [docs/Details.zh_cn.md](./docs/Details.zh_cn.md)。

## 开发

需要安装 Java 17 或更新版本，GNU gettext 和 Python 3.10 或更新版本。

要构建模组，

```sh
./gradlew build
```

构建出的 jar 文件位于 `build/libs/`。

要提取翻译键，

```sh
./gradlew extract
```

提取的翻译键位于 `build/messages.po`。翻译使用 [transifex](https://www.transifex.com/nkid00/redstone-computer-utilities) 托管。

要将翻译转换为 Minecraft 兼容的格式或从 Minecraft 兼容的格式转换，

```sh
python po2minecraft.py 文件路径/messages.po 文件路径/messages.json
python minecraft2po.py 文件路径/messages.json 文件路径/messages.po
```

要确定和 Minecraft 兼容的依赖版本，

```sh
python determine_deps.py
```

## 鸣谢

- [Fabric Loader](https://github.com/FabricMC/fabric-loader)，使用 [Apache-2.0](https://github.com/FabricMC/fabric-loader/blob/master/LICENSE) 许可证分发。
- [Fabric API](https://github.com/FabricMC/fabric)，使用 [Apache-2.0](https://github.com/FabricMC/fabric/blob/master/LICENSE) 许可证分发。
- [GSON](https://github.com/google/gson)，使用 [Apache-2.0](https://github.com/google/gson/blob/master/LICENSE) 许可证分发。
- [netty](https://github.com/netty/netty)，使用 [Apache-2.0](https://github.com/netty/netty/blob/4.1/LICENSE.txt) 许可证分发。
- [Guava](https://github.com/google/guava)，使用 [Apache-2.0](https://github.com/google/guava/blob/master/COPYING) 许可证分发。

## 版权

版权所有 © 2021-2022 NKID00

使用 [MPL-2.0](./LICENSE) 许可证分发。
