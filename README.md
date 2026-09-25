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

- **Directional weak signal / 方向性弱充能**
  - `top` slab powers **15 upward**, `bottom` slab powers **15 downward**, horizontal always **7**.
  - `double` slab powers **15** in every direction.
  - All are weak power only (strong power is `0`); the double slab is a non-conductor.
- **Redstone wire connection / 红石线连接**: driven by `isSignalSource`, so wires connect on all versions.
- **Half delay / 计划刻减半**: when the block directly below is a `top` redstone slab,
  - repeater `2/4/6/8 → 1/2/3/4`, comparator `2 → 1`, torch `2 → 1`, torch burnout `160 → 80`,
  - button `20/30 → 10/15`, detector rail `20 → 10`, pressure plate `20 → 10`,
  - coral `60..99 → 30..49`, fire `30..39 → 15..19`,
  - scaffolding: 50% run immediately, 50% schedule `1gt`,
  - water `5 → 2|3`, lava `30 → 15`.
- **Ice / snow doubling / 区块刻概率翻倍**: water above a `top` slab freezes, and snow above a `top`
  slab accumulates, at (approximately) double the vanilla rate.
- **Moving piston / 移塞**: a `moving_piston` above a `top` slab advances progress by `1` per tick.
- **Gravity / 重力**: a `top` slab falls as a custom falling entity. Falling into a `bottom` redstone
  slab merges it into a `double` slab; otherwise it solidifies as a `bottom` slab.
- **Comparator 7.5 / 比较器 7.5**: a slab as the front input reads `7`; as a side input it reads `8`.
  When both the front and a side are slabs, COMPARE outputs `7` and SUBTRACT outputs `0`.
- **Waterlogging / 含水**: slabs may be waterlogged; water is cleared when a falling slab lands/merges.
- **Crafting / 合成**: 3 redstone blocks → 6 slabs; stonecutter 1 redstone block → 2 slabs.
- Drops: single slab → 1, double slab → 2.

> **Warning / 警告**: the ice/snow doubling is implemented by adding an extra precipitation pass,
> which changes the world RNG sequence. Some vanilla randomness (random ticks etc.) may differ from
> the same seed without this mod. This is the accepted trade-off described in the design plan.
>
> 冰/雪翻倍通过追加一次降水判定实现，会改变世界随机数序列；同种子下部分原版随机行为
> （随机刻等）可能与本模组无关的原版不同。这是设计文档中已接受的风险。

## Differences from the existing "Redstone Slab" / 与既有 "Redstone Slab" 的区别

There is an unrelated closed-source NeoForge 1.21.1 mod named *Redstone Slab* that is essentially a
redstone block shaped like a slab. This project differs:

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

Requires Fabric Loader and Fabric API. Drop the jar for your Minecraft version into `mods/`.

## Acknowledgements / 致谢

- Build skeleton based on [fabric-mod-template](https://github.com/Fallen-Breath/fabric-mod-template)
  (ReplayMod preprocessor, LGPL-3.0).
- Design and compatibility notes reference `fabric-carpet`, `Carpet-TIS-Addition`, `SubTick`,
  `ticker`, `microtimingreplay` and `guardian` (read-only).

## License

LGPL-3.0. See `LICENSE`.
