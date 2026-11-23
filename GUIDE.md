# 禁忌学识

Minecraft 1.21.1 双加载器附属模组，为 RPG Series 的法杖、弓和弩提供可重复的锻造强化。

需要 AaTu Upmod Fabric `1.0.5` 或更高版本、或 NeoForge `1.0.6` 或更高版本（模组 ID：`aatu_exup`）及 RangedWeaponAPI `3.0.0` 或更高版本（模组 ID：`ranged_weapon_api`）；远程伤害属性保持为 `ranged_weapon:damage`。

## 获得方式

禁忌学识没有普通合成配方，也不会出现在创造模式物品栏。它仅会在玩家击杀的生物最大生命值达到 `drops.drop_minimum_health` 时，按 `drops.forbidden_knowledge_drop_chance` 概率掉落；默认分别为 `40` 与 `0.12`（12%）。玩家的间接伤害和归属到玩家的宠物击杀同样有效。

安装 JEI 后，属性强化书、注灵书和禁忌学识都会显示在 JEI 物品列表中，便于检索；这不会增加合成配方或改变掉落方式。

## 支持范围

- `BowItem` / `CrossbowItem`：只能强化 `ranged_weapon:damage`。
- Wizards 法杖：按法杖类型强化火焰、奥秘或寒冰法术强度，并可强化法术贯通；Wand 魔杖不接受强化。
- Paladins 治疗法杖：只能强化治疗强度，不会出现法术贯通词条；Wand 魔杖不接受强化。
- 盔甲、近战武器、材料和普通物品不会被强化配方接受。

## 使用方式

在锻造台中放入：

1. 模板槽：禁忌学识
2. 装备槽：受支持的法杖、弓或弩
3. 材料槽：配置文件指定的材料（默认下界之星）

每次消耗一本禁忌学识和一个材料，随机增加一项仍未达到累计上限的属性。原装备、附魔和数据组件都会保留。

装备提示中的禁忌强化属性与 AaTu Upmod 属性强化词条统一列在“强化属性总值”下。只有禁忌强化属性的法杖、弓或弩也会显示该标题。

## 注灵

法杖强化同时消耗 1 点注灵；未写入数据的法杖默认有 3 点，最大为 99 点。禁忌学识直接使用 AaTu Upmod 的 `AttributeData`，不存在第二套注灵余额。AaTu Upmod 的注灵书可作用于法杖，用其原有的可配置材料补充注灵。弓与弩的禁忌强化不消耗注灵。

## 法术贯通

法术贯通是百分比属性。火焰、奥秘或寒冰系法术造成伤害时，按法术贯通概率触发，额外造成：

`对应法术强度 × spell_penetration_damage_ratio`

该额外伤害在防御减免之后加入，默认比例为 `0.25`。

NeoForge 安装传奇怪物时，普通贯通和法术贯通的附加真实伤害都会单独结算，因此能穿过该模组 Boss 的单次伤害上限；原始攻击和原始法术伤害仍受其限制。

普通贯通与法术贯通的额外真实伤害默认不作用于玩家。可在 AaTu Upmod 统一配置中分别将 `attributes.penetration_affects_players` 和 `forbidden_knowledge.attributes.spell_penetration_affects_players` 设为 `true` 以开启。

## 配置

禁忌学识的全部配置均附加在 AaTu Upmod 的配置中：Fabric 为 `config/aatu_exup.properties`，NeoForge 为 `config/aatu_exup-common.toml`，键名位于 `forbidden_knowledge.*` 分组；不会再创建独立的禁忌学识配置文件。

治疗强度现在独立于火焰、奥秘和寒冰法术强度配置，可分别设置 `forbidden_knowledge.attributes.healing_min`、`healing_max` 与 `healing_limit`；默认值保持为 `0.5`、`1.5`、`20.0`。
