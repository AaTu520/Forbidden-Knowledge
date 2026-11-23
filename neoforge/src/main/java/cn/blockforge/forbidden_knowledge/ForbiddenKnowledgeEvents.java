package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import cn.blockforge.aatu_exup.AttributeData;
import cn.blockforge.aatu_exup.PenetrationDamage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public final class ForbiddenKnowledgeEvents {
    private static final String ENHANCEMENT_TOTAL = "tooltip.aatu_exup.enhancement_total";
    private static final String ATTRIBUTE_TOTAL = "tooltip.aatu_exup.attribute_total";

    /** Kept as a name so Legendary Monsters remains an optional dependency. */
    private static final String LEGENDARY_MONSTERS_BOSS_CLASS =
            "net.miauczel.legendary_monsters.entity.AnimatedMonster.OriginClasses.IAnimatedBoss";

    private ForbiddenKnowledgeEvents() {
    }

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(ForbiddenKnowledgeEvents::onItemAttributes);
        NeoForge.EVENT_BUS.addListener(ForbiddenKnowledgeEvents::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(ForbiddenKnowledgeEvents::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(ForbiddenKnowledgeEvents::onLivingDamagePost);
        NeoForge.EVENT_BUS.addListener(ForbiddenKnowledgeEvents::onLivingDrops);
        modBus.addListener(ForbiddenKnowledgeEvents::onEntityAttributes);
    }

    private static void onEntityAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ForbiddenKnowledgeMod.SPELL_PENETRATION, 0.0D);
    }

    private static void onItemAttributes(ItemAttributeModifierEvent event) {
        ForbiddenEnhancement.apply(event);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (ForbiddenEnhancement.isStaff(stack)) {
            event.getToolTip().add(Component.translatable("tooltip.forbidden_knowledge.soul", AttributeData.getSoul(stack))
                    .withStyle(ChatFormatting.GOLD));
        }

        var enhancements = ForbiddenAttributeData.getEnhancements(stack).stream()
                .filter(enhancement -> ForbiddenEnhancement.supportsType(stack, enhancement.type()))
                .toList();
        if (enhancements.isEmpty()) {
            return;
        }

        List<Component> tooltip = event.getToolTip();
        int headerIndex = enhancementHeaderIndex(tooltip);
        if (headerIndex < 0) {
            tooltip.add(Component.translatable(ENHANCEMENT_TOTAL).withStyle(ChatFormatting.GOLD));
            headerIndex = tooltip.size() - 1;
        }
        int insertIndex = headerIndex + 1;
        while (insertIndex < tooltip.size() && isAttributeLine(tooltip.get(insertIndex))) {
            insertIndex++;
        }
        for (ForbiddenAttributeData.Enhancement enhancement : enhancements) {
            tooltip.add(insertIndex++, Component.translatable(
                    ATTRIBUTE_TOTAL,
                    ForbiddenEnhancement.formatValue(enhancement.type(), enhancement.value()),
                    Component.translatable(ForbiddenEnhancement.attributeNameKey(enhancement.type())))
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    private static int enhancementHeaderIndex(List<Component> tooltip) {
        for (int index = 0; index < tooltip.size(); index++) {
            if (ENHANCEMENT_TOTAL.equals(translationKey(tooltip.get(index)))) {
                return index;
            }
        }
        return -1;
    }

    private static boolean isAttributeLine(Component line) {
        return ATTRIBUTE_TOTAL.equals(translationKey(line));
    }

    private static String translationKey(Component line) {
        return line.getContents() instanceof TranslatableContents contents ? contents.getKey() : "";
    }

    private static void onLivingDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        if (PenetrationDamage.isPenetration(source) || isLegendaryMonstersBoss(event.getEntity())) {
            return;
        }

        float extraDamage = SpellPenetration.additionalDamage(event.getEntity(), source);
        if (extraDamage > 0.0F) {
            event.setNewDamage(event.getNewDamage() + extraDamage);
        }
    }

    /**
     * Legendary Monsters caps regular hits before LivingDamageEvent.Pre. Apply
     * spell penetration after that hit with AaTu's tagged true-damage source.
     */
    private static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        if (PenetrationDamage.isPenetration(source) || !isLegendaryMonstersBoss(target)
                || !(source.getEntity() instanceof Player player)) {
            return;
        }

        float extraDamage = SpellPenetration.additionalDamage(target, source);
        if (extraDamage > 0.0F) {
            target.hurt(PenetrationDamage.source(target, player), extraDamage);
        }
    }

    private static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        // Mirrors AaTu Upmod's killed_by_player eligibility, including player-owned attacks.
        if (entity.level().isClientSide() || !(entity.getKillCredit() instanceof Player)
                || entity.getMaxHealth() < AaTu_EXUp_ModConfig.FORBIDDEN_KNOWLEDGE_DROP_MINIMUM_HEALTH.get()) {
            return;
        }

        if (entity.getRandom().nextDouble() < AaTu_EXUp_ModConfig.FORBIDDEN_KNOWLEDGE_BOOK_DROP_CHANCE.get()) {
            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(),
                    new ItemStack(ForbiddenKnowledgeMod.FORBIDDEN_KNOWLEDGE.get())));
        }
    }

    private static boolean isLegendaryMonstersBoss(LivingEntity entity) {
        for (Class<?> type = entity.getClass(); type != null; type = type.getSuperclass()) {
            if (LEGENDARY_MONSTERS_BOSS_CLASS.equals(type.getName())) {
                return true;
            }
        }
        return false;
    }
}
