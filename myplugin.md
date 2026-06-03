# XSex — Minecraft Spigot 插件项目详解

## 项目概览

| 属性 | 值 |
|------|-----|
| **项目名称** | XSex |
| **版本** | 1.0-SNAPSHOT |
| **作者** | ChickenDJRsiri |
| **Minecraft API** | Spigot 1.18.2 (api-version 1.18) |
| **Java 版本** | 17 |
| **构建工具** | Gradle (Groovy DSL) |
| **包名** | `org.ChickenDJR.xSex` |
| **GitHub** | https://github.com/DJRsiri |

这是一个 **Minecraft Spigot 服务器插件**，在游戏中模拟两名玩家之间的成人互动（SOX），包含完整的请求/接受机制、动画效果、冷却系统、随机结局及数据持久化。

---

## 项目结构

```
XSex/
├── build.gradle                        # Gradle 构建配置
├── settings.gradle                     # 项目名称定义
├── gradle.properties                   # Gradle 属性
├── gradlew / gradlew.bat              # Gradle Wrapper
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
└── src/main/
    ├── java/org/ChickenDJR/xSex/
    │   ├── Main.java                   # 插件主入口 → 第1-98行
    │   ├── Sex.java                    # 单次互动会话 → 第1-145行
    │   ├── SexManager.java            # 全局管理器 → 第1-247行
    │   ├── SexPlayer.java             # 玩家数据模型 → 第1-46行
    │   └── Listeners.java             # 事件监听器 → 第1-47行
    └── resources/
        ├── plugin.yml                  # 插件元数据
        └── config.yml                  # 消息/本地化配置
```

---

## 核心类详解

### 1. `Main.java` — 插件入口

继承 `JavaPlugin`，是插件生命周期和命令处理的中心：

- **`onEnable()`**（第16-21行）：插件启动时保存默认配置，注册事件监听器，初始化 `SexManager`。
- **`onDisable()`**（第23-25行）：调用 `SexManager.stop()` 中断所有进行中的互动。
- **`onCommand()`**（第35-97行）：处理 `/sex` 命令及其子命令：
  - `/sex <玩家名>` — 发送 SOX 请求
  - `/sex accept <玩家名>` — 接受请求
  - `/sex deny <玩家名>` — 拒绝请求
  - `/sex gender male/female` — 设置性别
  - `/sex`（无参数）— 显示帮助信息与统计数据

命令分发通过字符串 `hashCode()` 进行 `switch` 跳转，这是 Java 编译器对 `switch(string)` 的底层实现。

### 2. `Sex.java` — 互动会话

代表一次正在进行的双人互动，核心行为：

- **`start()`**（第31-71行）：
  - 向双方发送 Title 和聊天消息
  - 施加 **220 tick（11秒）失明效果**
  - 启动 **每秒执行** 的 `effects` 定时任务：
    - 双方每 tick 产生爱心粒子效果 (Particle.HEART)
    - 播放猫呼噜声 (Sound.ENTITY_CAT_PURR)
    - 逐秒发送阶段性对话消息（共 10 步）
  - 启动 **208 tick（10.4秒）** 后的 `end` 定时任务
  - 中途若任一玩家离线或距离超过 ~3.16 格（`distanceSquared >= 10`），则中断

- **`end()`**（第73-125行）— 随机结局（5种，等概率）：
  | 结局 | 男性效果 | 女性效果 | 特殊事件 |
  |------|---------|---------|----------|
  | 0 | 虚弱 + 中毒 (15s) | 虚弱 (15s) | 得病 |
  | 1 | 生命恢复 (30s) | 生命恢复 (30s) | 贤者时间 |
  | 2 | 无药水效果 | 无药水效果 | 全服广播 + `/tempban` 男性 8 秒（强奸罪） |
  | 3 | 虚弱 (15s) | 生命恢复 (30s) | 口交情节 |
  | 4 | 虚弱 (15s) | 虚弱 (15s) | 生成幼年村民「孩子」+ 全服广播 |

  - 结局后：双方加入 60 秒冷却，互动计数 +1

- **`cancel()`**（第133-144行）：移除失明效果，从活跃列表中清除双方。

- **`extraCancel()`**（第127-131行）：在取消定时任务的基础上额外调用 `cancel()`。

### 3. `SexManager.java` — 全局管理器

核心数据持有者，维护以下 HashMap：

| 字段 | 类型 | 用途 |
|------|------|------|
| `players` | `HashMap<String, SexPlayer>` | 玩家名 → 玩家数据对象 |
| `alreadySex` | `HashMap<Player, Sex>` | 正在互动中的玩家映射 |
| `requests` | `HashMap<String, String>` | 请求者 → 被请求者 |
| `cooldowns` | `HashMap<String, Integer>` | 玩家名 → 冷却剩余秒数 |
| `timeout` | `HashMap<String, Integer>` | 请求者 → 请求超时倒计时 |

- **构造器**（第19-59行）：启动两个异步每秒定时器：
  - **冷却倒计时**：每秒 -1，归零时移除
  - **请求超时倒计时**：每秒 -1，归零时同时清除请求和超时记录

- **`sendRequest()`**（第180-246行）：发送请求前的多重校验：
  1. 不能请求自己
  2. 请求者不能在冷却中
  3. 请求者不能正在互动中
  4. 请求者不能有待处理的请求（60秒超时）
  5. 目标必须在线
  6. 双方必须已设置性别
  7. 双方性别必须不同（异性）
  8. 距离必须 ≤ 3.16 格

- **`acceptRequest()`**（第109-164行）：与 `sendRequest` 对称的校验逻辑，额外验证请求存在且匹配。

- **`denyRequest()`**（第167-177行）：验证请求存在后移除并通知。

- **`checkGender()`**（第99-106行）：根据请求者的性别决定 `sex(male, female)` 的参数顺序。

- **`stop()`**（第63-72行）：插件卸载时遍历并取消所有进行中的互动。

- **`loadPlayer()` / `getPlayer()`**：玩家数据的懒加载与获取。

### 4. `SexPlayer.java` — 玩家数据模型

| 字段 | 类型 | 说明 |
|------|------|------|
| `player` | `Player` | Bukkit Player 引用 |
| `gender` | `int` | 0=未指定, 1=男性, 2=女性 |
| `amount` | `int` | 累计互动次数 |

- 数据持久化到 `config.yml` 的 `players.<玩家名>.gender` 和 `players.<玩家名>.amount` 路径。
- 每次 `setGender()` 和 `addAmount()` 后立即调用 `saveConfig()` 保存。

### 5. `Listeners.java` — 事件监听器

| 事件 | 处理逻辑 |
|------|---------|
| `PlayerJoinEvent` | 调用 `loadPlayer()` 加载玩家数据 |
| `PlayerQuitEvent` | 从 `players` 中移除该玩家 |
| `PlayerKickEvent` | 从 `players` 中移除该玩家 |
| `EntityDamageEvent` | 若受伤实体是在互动中的玩家，强制取消互动 |

---

## 配置文件 (`config.yml`)

全部消息使用 Minecraft 颜色代码（`§d` 粉色、`§f` 白色、`§7` 灰色、`§a` 绿色、`§l` 加粗），内容为中文（含日语口癖如「❤」「哎多」「呜…」）。

关键配置路径：
- `messages.start` — 互动开始消息
- `messages.sex-male-{1..10}` / `messages.sex-female-{1..10}` — 10 秒阶段性对话
- `messages.end-male-{1..5}` / `messages.end-female-{1..5}` — 5 种结局消息
- `messages.requests.*` — 请求流程消息模板
- `messages.help` — 帮助信息列表
- `messages.error-{1..13}` — 13 种错误提示
- `messages.end-command-3` — 结局 3 执行的命令模板（`tempban %player 8s 强奸`）

---

## 命令参考

| 命令 | 功能 |
|------|------|
| `/sex` | 查看帮助、累计次数和性别 |
| `/sex <玩家名>` | 向指定玩家发送 SOX 请求 |
| `/sex accept <玩家名>` | 接受玩家的 SOX 请求 |
| `/sex deny <玩家名>` | 拒绝玩家的 SOX 请求 |
| `/sex gender male` | 设置性别为男性 |
| `/sex gender female` | 设置性别为女性 |

---

## 架构设计要点

1. **性别系统**：必须双方异性（男+女）才能互动，这是一种简化的二元性别模型。
2. **冷却与超时**：每次互动后 60 秒冷却；请求 60 秒未响应自动过期。
3. **距离约束**：双方距离必须在 ~3.16 格（`sqrt(10)`）以内，互动过程中离开则中断。
4. **中断机制**：玩家受伤、离线、超距均会触发互动中断。
5. **数据持久化**：玩家性别和互动次数写入 `config.yml`，无独立数据库。
6. **异步定时器**：冷却和超时倒计时在异步任务中执行，不阻塞主线程。
7. **状态隔离**：同一玩家不能同时处于多个互动中（`alreadySex` HashMap 保证唯一）。

---

## 构建与运行

```bash
# 使用 Gradle Wrapper 构建
./gradlew build

# 产物位于
# build/libs/XSex-1.0-SNAPSHOT.jar
```

将生成的 JAR 文件放入 Spigot/Paper 1.18.x 服务器的 `plugins/` 目录，重启或执行 `/reload` 即可加载。
