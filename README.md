# Redstone Slab / 红石台阶

**English** | [中文](#中文)

A Fabric mod that adds a directional redstone slab with half-delay semantics, gravity, comparator
`7.5` behaviour and waterlogging.

一个 Fabric 模组：加入**有方向性的红石台阶**，并带来计划刻/液体刻减半、重力、比较器 `7.5` 语义与含水能力。

Supported versions / 支持版本:

| MC | Status |
|---|---|
| 1.21.11 | main |
| 1.21.10 | yes |
| 1.21.1 | yes |
| 1.19.4 | yes |
| 26.3 | yes |

## Features / 功能

- **Directional signal / 方向性信号**
  - `top` slab powers **15 upward**, `bottom` slab powers **15 downward**, horizontal always **7**.
  - `double` slab powers **15** in every direction.
  - The slab also emits direct (strong) power in those directions, so an adjacent redstone conductor is
    charged and can activate non-wire components (pistons, lamps, doors, repeaters, ...). A conductor
    charged by the slab does **not** power redstone wire; redstone wire that is directly adjacent to the
    slab is still powered by the slab's weak signal. The double slab is a non-conductor.
- **Redstone wire connection / 红石线连接**: driven by `isSignalSource`, so wires connect on all versions.
- **Half delay / 计划刻减半**: when the block directly below is a `top` redstone slab,
  - repeater `2/4/6/8 → 1/2/3/4`, comparator `2 → 1`, torch `2 → 1`, torch burnout `160 → 80`,
  - button `20/30 → 10/15`, detector rail `20 → 10`, pressure plate `20 → 10`,
  - coral `60..99 → 30..49`, fire `30..39 → 15..19`,
  - scaffolding: 50% run immediately, 50% schedule `1gt`,
  - water `5 → 2|3`, lava `30 → 15`.
- **Ice / snow doubling / 区块刻概率翻倍**: water above a `top` slab freezes, and snow above a `top`
  slab accumulates, at (approximately) double the vanilla rate.
- **Moving piston / 移塞**: a `moving_piston` above a `top` slab whose contained block needs downward
  support advances progress by `1` per tick.
- **Gravity / 重力**: a `top` slab falls as a custom falling entity whose collision box is the upper
  8px. Falling into a `bottom` redstone slab merges it into a `double` slab, unless a living entity
  blocks it at the 10px plane; otherwise it solidifies as a `bottom` slab.
- **Comparator 7.5 / 比较器 7.5**: a single slab as the front input reads `7`; as a side input it reads `8`.
  Each input may combine several sources: if any source reaches `8` or more, the normal integer comparison
  is used; otherwise a 7.5 source is treated as the strongest. When the front and a side both read 7.5
  (and the other side is absent, also a slab, or below 8), COMPARE outputs `7` and SUBTRACT outputs `0`.
  The front also counts as 7.5 when it is a redstone conductor charged by a single slab's horizontal side
  charge (7) with nothing reaching 8; a container / item frame read through the front takes precedence.
  Double slabs are never 7.5.
  - 单个红石台阶作正面输入按 `7`、作侧面输入按 `8`。比较器的两侧输入与正面（经充能方块）都可汇总多个
    来源：只要有任一来源 ≥8 就按正常整数比较，否则 7.5 来源作为最强信号参与特判。正面与某一侧同为 7.5
    （且另一侧无输入、也是单层台阶或为 <8 的其他输入）时，比较模式输出 `7`、减法模式输出 `0`。正面为
    「被单层台阶水平侧面充能且无 ≥8 来源」的导体时同样按 7.5 处理；经正面读到容器/展示框容量时以容量为准。
    双半砖不参与 7.5。
- **Waterlogging / 含水**: slabs may be waterlogged; water is cleared when a falling slab lands/merges.
- **Crafting / 合成**: 3 redstone blocks → 6 slabs; stonecutter 1 redstone block → 2 slabs.
- Drops: single slab → 1, double slab → 2.

> **Warning / 警告**: the ice/snow doubling is implemented by adding an extra precipitation roll
> restricted to qualifying positions (1-in-48 on 1.21.1+, 1-in-16 on 1.19.4), so the effective
> probability is about double vanilla. This consumes extra world RNG, so some vanilla randomness
> (random ticks etc.) may differ from the same seed without this mod. This is the accepted trade-off
> described in the design plan.
>
> 冰/雪翻倍通过为满足条件的位置追加一次降水判定实现（1.21.1+ 为 1/48，1.19.4 为 1/16），等效概率约为原版
> 两倍。该实现会额外消耗世界随机数；同种子下部分原版随机行为（随机刻等）可能与无本模组的原版不同。这是
> 设计文档中已接受的风险。

## Compatibility / 兼容性

- **Lithium**: Lithium replaces the vanilla redstone wire power calculation, which bypasses this
  mod's weak-charging guard and would let a slab-charged conductor power redstone wire. This mod
  declares `custom.lithium:options = { "mixin.block.redstone_wire": false }` in its `fabric.mod.json`,
  so Lithium disables only that one redstone-wire optimisation while this mod is installed. All other
  Lithium optimisations are unaffected.
- **Carpet / Carpet-TIS (not covered)**: `fastRedstoneDust` / `RedstoneWireTurbo` perform their own
  wire calculation and cannot be disabled the same way. A slab-charged conductor may power redstone
  wire under those rules; disable the corresponding option if it matters.

## Differences from the existing "Redstone Slab" / 与既有 "Redstone Slab" 的区别

There is an unrelated closed-source NeoForge mod named
[Redstone Slab](https://modrinth.com/mod/redstone-slab) (by iMacJack) that is essentially a redstone
block shaped like a slab. This project differs:

1. Directional signal (`top` 15 up / 7 side, `bottom` 15 down / 7 side, `double` 15 everywhere) instead of all-direction 15.
2. Comparator `7.5` semantics.
3. Half-delay system (planned ticks, fluids, scaffolding, chunk ticks, moving piston).
4. Gravity with a custom falling entity and bottom-slab merging.
5. Different crafting (3→6 or stonecutter 1→2) and block properties (METAL sound, 5.0/6.0 strength, any pickaxe).
6. Multi-version and open source (LGPL-3.0).

## Build / 构建

Requirements: JDK 21 for MC < 26.x, JDK 25 for 26.x.

```bash
./gradlew buildAndGather          # all versions
./gradlew :1.21.11:build          # one version
JAVA_HOME=/path/to/jdk25 ./gradlew :26.3:build
```

Output jars are in `versions/<version>/build/libs/` (and gathered into `build/libs/`).

## Install / 安装

Requires Fabric Loader **0.15.0+** and Fabric API. Drop the jar for your Minecraft version into `mods/`.
MixinExtras is used for the redstone-wire weak-charging guard and is provided by Fabric Loader (bundled
since 0.15.0), so no extra dependency is needed.

需要 Fabric Loader **0.15.0+** 与 Fabric API。把对应 Minecraft 版本的 jar 放进 `mods/` 即可。红石线弱充能
护栏使用 MixinExtras，它自 Fabric Loader 0.15.0 起随 Loader 提供，无需额外依赖。

## Credits / 致谢

- Design inspired by [this video](https://www.bilibili.com/video/BV1ogkcYVEiN/) (a redstone slab that
  emits `7.5` redstone power and halves the delay of the components attached to it).
- Build skeleton based on [fabric-mod-template](https://github.com/Fallen-Breath/fabric-mod-template),
  using the [ReplayMod preprocessor](https://github.com/ReplayMod/preprocessor) /
  [Fallen-Breath preprocessor](https://github.com/Fallen-Breath/preprocessor) and
  [yamlang](https://github.com/Fallen-Breath/yamlang).
- Design and compatibility notes reference these projects (read-only):
  - [fabric-carpet](https://github.com/gnembon/fabric-carpet) and
    [Carpet-TIS-Addition](https://github.com/TISUnion/Carpet-TIS-Addition)
  - [SubTick](https://github.com/lntricate1/SubTick)
  - [ticker](https://github.com/hotpad100c/ticker)
  - [microtimingreplay](https://github.com/hotpad100c/microtimingreplay)
  - [ModMenu](https://github.com/TerraformersMC/ModMenu)
- Built with [Fabric Loader](https://github.com/FabricMC/fabric-loader),
  [Fabric API](https://github.com/FabricMC/fabric) and
  [Fabric Loom](https://github.com/FabricMC/fabric-loom).

## License

LGPL-3.0. See `LICENSE`.
