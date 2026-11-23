package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class SpellPenetration {
    private static final ResourceKey<DamageType> FIRE = damageType("fire");
    private static final ResourceKey<DamageType> ARCANE = damageType("arcane");
    private static final ResourceKey<DamageType> FROST = damageType("frost");
    private static final ResourceKey<DamageType> HEALING = damageType("healing");

    private SpellPenetration() {
    }

    public static float additionalDamage(LivingEntity target, DamageSource source) {
        if (!(source.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return 0.0F;
        }
        if (target instanceof Player
                && !AaTu_EXUp_ModConfig.FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_AFFECTS_PLAYERS.getAsBoolean()) {
            return 0.0F;
        }

        String powerType = powerType(source);
        if (powerType == null) {
            return 0.0F;
        }

        double chance = player.getAttributeValue(ForbiddenKnowledgeMod.SPELL_PENETRATION);
        if (chance <= 0.0D || player.getRandom().nextDouble() >= chance) {
            return 0.0F;
        }

        var powerAttribute = ForbiddenEnhancement.attributeFor(powerType);
        if (powerAttribute == null) {
            return 0.0F;
        }
        double damage = player.getAttributeValue(powerAttribute)
                * AaTu_EXUp_ModConfig.FORBIDDEN_KNOWLEDGE_SPELL_PENETRATION_DAMAGE_RATIO.getAsDouble();
        if (!Double.isFinite(damage) || damage <= 0.0D) {
            return 0.0F;
        }
        return (float) Math.min(damage, Float.MAX_VALUE);
    }

    private static String powerType(DamageSource source) {
        if (source.is(FIRE)) {
            return ForbiddenEnhancement.FIRE;
        }
        if (source.is(ARCANE)) {
            return ForbiddenEnhancement.ARCANE;
        }
        if (source.is(FROST)) {
            return ForbiddenEnhancement.FROST;
        }
        if (source.is(HEALING)) {
            return ForbiddenEnhancement.HEALING;
        }
        return null;
    }

    private static ResourceKey<DamageType> damageType(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath("spell_power", path));
    }
}
