package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AttributeData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ForbiddenKnowledgeClient implements ClientModInitializer {
    private static final String ENHANCEMENT_TOTAL = "tooltip.aatu_exup.enhancement_total";
    private static final String ATTRIBUTE_TOTAL = "tooltip.aatu_exup.attribute_total";

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> addEnhancementTooltip(stack, lines));
    }

    private static void addEnhancementTooltip(ItemStack stack, List<Component> lines) {
        if (ForbiddenEnhancement.isStaff(stack)) {
            lines.add(Component.translatable("tooltip.forbidden_knowledge.soul", AttributeData.getSoul(stack))
                    .withStyle(ChatFormatting.GOLD));
        }

        var enhancements = ForbiddenAttributeData.getEnhancements(stack).stream()
                .filter(enhancement -> ForbiddenEnhancement.supportsType(stack, enhancement.type()))
                .toList();
        if (enhancements.isEmpty()) {
            return;
        }

        int headerIndex = enhancementHeaderIndex(lines);
        if (headerIndex < 0) {
            lines.add(Component.translatable(ENHANCEMENT_TOTAL).withStyle(ChatFormatting.GOLD));
            headerIndex = lines.size() - 1;
        }
        int insertIndex = headerIndex + 1;
        while (insertIndex < lines.size() && isAttributeLine(lines.get(insertIndex))) {
            insertIndex++;
        }
        for (ForbiddenAttributeData.Enhancement enhancement : enhancements) {
            lines.add(insertIndex++, Component.translatable(
                    ATTRIBUTE_TOTAL,
                    ForbiddenEnhancement.formatValue(enhancement.type(), enhancement.value()),
                    Component.translatable(ForbiddenEnhancement.attributeNameKey(enhancement.type())))
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    private static int enhancementHeaderIndex(List<Component> lines) {
        for (int index = 0; index < lines.size(); index++) {
            if (ENHANCEMENT_TOTAL.equals(translationKey(lines.get(index)))) {
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
}
