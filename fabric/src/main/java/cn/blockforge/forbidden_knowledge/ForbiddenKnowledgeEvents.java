package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ForbiddenKnowledgeEvents {
    private ForbiddenKnowledgeEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(ForbiddenKnowledgeEvents::onLivingDeath);
    }

    private static void onLivingDeath(
            LivingEntity entity, net.minecraft.world.damagesource.DamageSource ignoredSource) {
        // Mirrors AaTu Upmod's killed_by_player eligibility, including player-owned attacks.
        if (!(entity.getKillCredit() instanceof Player)
                || entity.getMaxHealth() < AaTu_EXUp_ModConfig.FORBIDDEN_KNOWLEDGE_DROP_MINIMUM_HEALTH.getAsDouble()) {
            return;
        }

        if (entity.getRandom().nextDouble() < AaTu_EXUp_ModConfig.FORBIDDEN_KNOWLEDGE_BOOK_DROP_CHANCE.getAsDouble()) {
            entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(ForbiddenKnowledgeMod.FORBIDDEN_KNOWLEDGE)));
        }
    }
}
