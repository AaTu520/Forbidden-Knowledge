package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ForbiddenAttributeData {
    private static final String ROOT = "forbidden_knowledge_enhancements";
    private static final String ATTRIBUTES = "attributes";
    private static final String TYPE = "type";
    private static final String VALUE = "value";

    private ForbiddenAttributeData() {
    }

    public record Enhancement(String type, double value) {
    }

    public static List<Enhancement> getEnhancements(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        if (tag == null || !tag.contains(ATTRIBUTES, Tag.TAG_LIST)) {
            return List.of();
        }

        Map<String, Double> totals = new LinkedHashMap<>();
        ListTag entries = tag.getList(ATTRIBUTES, Tag.TAG_COMPOUND);
        for (int index = 0; index < entries.size(); index++) {
            CompoundTag entry = entries.getCompound(index);
            String type = entry.getString(TYPE);
            double value = entry.getDouble(VALUE);
            if (!ForbiddenEnhancement.isKnownType(type) || !Double.isFinite(value) || value <= 0.0D) {
                continue;
            }
            double total = Math.min(limit(type), totals.getOrDefault(type, 0.0D) + value);
            if (total > 0.0D) {
                totals.put(type, total);
            }
        }

        List<Enhancement> result = new ArrayList<>();
        totals.forEach((type, value) -> result.add(new Enhancement(type, value)));
        return List.copyOf(result);
    }

    public static double getEnhancementValue(ItemStack stack, String type) {
        for (Enhancement enhancement : getEnhancements(stack)) {
            if (enhancement.type().equals(type)) {
                return enhancement.value();
            }
        }
        return 0.0D;
    }

    public static double add(ItemStack stack, String type, double value) {
        if (!ForbiddenEnhancement.isKnownType(type) || !Double.isFinite(value) || value <= 0.0D) {
            return 0.0D;
        }

        List<Enhancement> enhancements = new ArrayList<>(getEnhancements(stack));
        double current = getEnhancementValue(stack, type);
        double total = Math.min(limit(type), current + value);
        double added = total - current;
        if (added <= 0.0D) {
            return 0.0D;
        }

        boolean replaced = false;
        for (int index = 0; index < enhancements.size(); index++) {
            if (enhancements.get(index).type().equals(type)) {
                enhancements.set(index, new Enhancement(type, total));
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            enhancements.add(new Enhancement(type, total));
        }
        setEnhancements(stack, enhancements);
        return added;
    }

    private static void setEnhancements(ItemStack stack, List<Enhancement> enhancements) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> {
            CompoundTag data = new CompoundTag();
            ListTag entries = new ListTag();
            for (Enhancement enhancement : enhancements) {
                double value = Math.min(limit(enhancement.type()), Math.max(0.0D, enhancement.value()));
                if (!ForbiddenEnhancement.isKnownType(enhancement.type()) || !Double.isFinite(value) || value <= 0.0D) {
                    continue;
                }
                CompoundTag entry = new CompoundTag();
                entry.putString(TYPE, enhancement.type());
                entry.putDouble(VALUE, value);
                entries.add(entry);
            }
            data.put(ATTRIBUTES, entries);
            root.put(ROOT, data);
        });
    }

    private static double limit(String type) {
        double value = AaTu_EXUp_ModConfig.forbiddenKnowledgeAttributeLimit(type);
        return Double.isFinite(value) ? Math.max(0.0D, value) : 0.0D;
    }

    private static CompoundTag getTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        CompoundTag root = customData.copyTag();
        if (!root.contains(ROOT, Tag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag tag = root.getCompound(ROOT);
        return tag.isEmpty() ? null : tag;
    }
}
