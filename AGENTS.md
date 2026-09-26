# AGENTS.md

本文件给后续参与本仓库的开发者 / AI 代理使用。
This file is for future developers / AI agents working on this repository.

## 项目 / Project

- mod id: `redstoneslab`，包根 `com.redstoneslab`，显示名 `红石台阶 / Redstone Slab`。
- 主版本 (mainProject): **1.21.11**。目标版本: `1.21.11`、`1.21.10`、`1.21.1`、`26.3`、`1.19.4`。
- 构建骨架来自 `fabric-mod-template` 的 ReplayMod preprocessor 方案。
- 许可: LGPL-3.0。

## 构建 / Build

- MC < 26.x 使用 JDK 21；26.x 使用 JDK 25（设置 `JAVA_HOME` 指向 JDK 25）。
- 全版本: `./gradlew buildAndGather`
- 单版本: `./gradlew :1.21.11:build`
- 26.3: `JAVA_HOME=<jdk25> ./gradlew :26.3:build`
- 服务端冒烟测试: `./gradlew :1.21.11:runServer`（需 `run/eula.txt` 中 `eula=true`）
- 产物: `versions/<ver>/build/libs/`，`buildAndGather` 会汇总到根 `build/libs/`。

## 版本布局 / Version layout

- `settings.json` 列出启用版本；`build.gradle` 建立 preprocess 节点；每版本 `versions/<ver>/gradle.properties`。
- 共用源码在 `src/main`。ReplayMod preprocessor 语法：
  - `//#if MC >= 12110` … `//#elseif MC >= 12000` … `//#else` … `//#endif`
  - **主版本（1.21.11）所走的分支必须写成普通行（不带 `//$$`）**，其余分支每行前缀 `//$$`。
  - 主工程不跑 preprocessor，所以 `src/main` 必须能直接以 1.21.11 编译。
  - 预处理后每行 `//#` 仍以注释保留，`//$$` 行是未激活分支，属正常现象。
- `versions/mainProject` 内容为 `1.21.11`。

## Mixin 规则 / Mixin rules

- 配置: `src/main/resources/redstoneslab.mixins.json`。
- 通用 mixin 放 `com.redstoneslab.mixin`；版本相关放 `com.redstoneslab.mixin.modern` / `.legacy`。
- 跨版本通用 mixin：`DiodeBlockMixin`（比较器 7.5，注入 `DiodeBlock.getAlternateSignal`）、`ServerLevelTickMixin`（区块刻冰/雪翻倍）。
- 不存在 MixinConfigPlugin；版本切换靠 `//#if` + 每个文件在另一版本编译成合法的空 `@Mixin` 占位类。
- 现代：`ScheduledTickAccess` 默认方法（`scheduleTick`）；旧版：`LevelAccessor` 默认方法。
- 计划刻减半集中在 `ModernScheduleTickMixin` / `LegacyScheduleTickMixin`，白名单见 `util/DelayPolicy`。
- 方块类型判断统一用 `util/HalfDelay`（下方是否为 `top` 台阶）。
- 红石线弱充能护栏用 MixinExtras `@WrapMethod`（`modern.RedstoneWireBlockMixin` / `legacy.LegacyRedstoneWireBlockMixin`），以保证 try/finally；**不用** `@Redirect`。MixinExtras 自 Fabric Loader 0.15.0 起随 Loader 提供，故 `fabric.mod.json` 要求 `fabricloader >=0.15.0`。

## 许可与拷贝 / License & copying

- `guardian`（反编译源码）只读参考，**禁止拷贝**。
- 参考 `fabric-mod-template`、`fabric-carpet`、`Carpet-TIS-Addition`、`SubTick`、`ticker`、`microtimingreplay` 时遵守其 LGPL 条款。
