# Whitelist4QQ
在QQ群内自助申请Minecraft白名单！

## 介绍

#### 什么是 Whitelist4QQ？
Whitelist4QQ 是一个基于 [MiraiMC](https://github.com/DreamVoid/MiraiMC) 的 Bukkit 插件，能够让玩家在 QQ 群内自助申请 Minecraft 白名单。

## 下载
 * [Github 发布页](https://github.com/DreamVoid/Whitelist4QQ/releases)
 * [Gitee 发布页](https://gitee.com/dreamvoid/Whitelist4QQ/releases)（中国）
 * [MCBBS](https://www.mcbbs.net/thread-1271011-1-1.html)（中国）

## 开始使用

* 下载插件，并将插件文件放入 plugins 文件夹；
* 下载 [MiraiMC](https://github.com/DreamVoid/MiraiMC) 插件（如果尚未下载），并将插件文件放入 plugins 文件夹；
* 启动服务端（如果尚未启动）或使用诸如 PlugMan 的插件加载插件；
* 使用指令“**/mirai login <账号> <密码>**”登录你的机器人账号；
* 调整插件的配置文件；
* 重新启动服务器（请不要热重载，可能会出现问题）；
* 享受插件吧！

## 配置文件

默认配置文件可以在这里找到：https://github.com/DreamVoid/Whitelist4QQ/blob/main/src/main/resources/config.yml
## 指令和权限
### 指令
| 命令 | 描述 | 权限 | 别名 |
| ---------------------------- | ---------------------- | ---------- | ------- |
| /whitelist4qq  | 插件主命令 | whitelist4qq.command.whitelist4qq | qwl, qwhitelist |
| /whitelist4qq reload | 重新加载配置文件 | whitelist4qq.command.whitelist4qq.reload |

### 权限
| 权限节点 | 描述 | 默认 |
| ---------------------------- | ---------------------- | ---------- |
| whitelist4qq.command.whitelist4qq | 允许使用 /whitelist4qq | YES |
| whitelist4qq.command.whitelist4qq.reload | 允许使用 /whitelist4qq reload | OP |
