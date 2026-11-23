package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class ForbiddenKnowledgeBookItem extends Item {
    private static final String DIVIDER = "-----------------------------------";

    public ForbiddenKnowledgeBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack).copy().withStyle(ChatFormatting.RED);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(DIVIDER).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.forbidden_knowledge.forbidden_knowledge.lore_flavor")
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("item.forbidden_knowledge.forbidden_knowledge.lore_use")
                .withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(DIVIDER).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.forbidden_knowledge.forbidden_knowledge.lore_cost",
                AaTu_EXUp_ModConfig.forbiddenKnowledgeEnhancementCostItemName().copy()
                        .withStyle(ChatFormatting.WHITE))
                .withStyle(ChatFormatting.WHITE)
                .append(Component.translatable("item.forbidden_knowledge.forbidden_knowledge.lore_point")
                        .withStyle(ChatFormatting.WHITE)));
        tooltip.add(Component.translatable("item.forbidden_knowledge.forbidden_knowledge.lore_penetration")
                .withStyle(ChatFormatting.RED));
    }
}
