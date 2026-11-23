package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public final class ForbiddenEnhancement {
    public static final String FIRE = "spell_power:fire";
    public static final String ARCANE = "spell_power:arcane";
    public static final String FROST = "spell_power:frost";
    public static final String RANGED_DAMAGE = "ranged_weapon:damage";
    public static final String HEALING = "spell_power:healing";
    public static final String SPELL_PENETRATION = "forbidden_knowledge:spell_penetration";

    private static final Class<?> STAFF_ITEM_CLASS = resolveStaffItemClass();

    private ForbiddenEnhancement() {
    }

    public record Roll(String type, double value) {
    }

    public static boolean isUpgradeable(ItemStack stack) {
        return !supportedTypes(stack).isEmpty();
    }

    static boolean hasAvailableAttribute(ItemStack stack, List<ForbiddenAttributeData.Enhancement> enhancements) {
        for (String type : supportedTypes(stack)) {
            if (remainingCapacity(type, enhancements) > 0.0D) {
                return true;
            }
        }
        return false;
    }

    static Roll nextRoll(ItemStack stack, List<ForbiddenAttributeData.Enhancement> enhancements) {
        RandomSource random = RandomSource.create(seed(stack));
        List<String> available = new ArrayList<>();
        for (String type : supportedTypes(stack)) {
            if (remainingCapacity(type, enhancements) > 0.0D) {
                available.add(type);
            }
        }
        if (available.isEmpty()) {
            return null;
        }

        String type = available.get(random.nextInt(available.size()));
        double value = Math.min(AaTu_EXUp_ModConfig.randomForbiddenKnowledgeAttributeValue(type, random),
                remainingCapacity(type, enhancements));
        return value > 0.0D ? new Roll(type, value) : null;
    }

    private static List<String> supportedTypes(ItemStack stack) {
        if (stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem) {
            return List.of(RANGED_DAMAGE);
        }
        if (!isStaff(stack)) {
            return List.of();
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) {
            return List.of();
        }
        if ("paladins".equals(itemId.getNamespace())) {
            return List.of(HEALING);
        }
        if (!"wizards".equals(itemId.getNamespace())) {
            return List.of();
        }

        String path = itemId.getPath().toLowerCase(Locale.ROOT);
        if (path.contains("fire")) {
            return List.of(FIRE, SPELL_PENETRATION);
        }
        if (path.contains("arcane")) {
            return List.of(ARCANE, SPELL_PENETRATION);
        }
        if (path.contains("frost") || path.contains("ice")) {
            return List.of(FROST, SPELL_PENETRATION);
        }
        return List.of(FIRE, ARCANE, FROST, SPELL_PENETRATION);
    }

    public static void applyModifiers(
            ItemStack stack, EquipmentSlot slot, BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
        List<String> types = supportedTypes(stack);
        if (types.isEmpty() || (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND)) {
            return;
        }
        for (ForbiddenAttributeData.Enhancement enhancement : ForbiddenAttributeData.getEnhancements(stack)) {
            if (!types.contains(enhancement.type())) {
                continue;
            }
            Holder<Attribute> attribute = attributeFor(enhancement.type());
            if (attribute != null) {
                consumer.accept(attribute,
                        new AttributeModifier(modifierId(stack, enhancement.type()), enhancement.value(),
                                AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    public static boolean isKnownType(String type) {
        return switch (type) {
            case FIRE, ARCANE, FROST, RANGED_DAMAGE, HEALING, SPELL_PENETRATION -> true;
            default -> false;
        };
    }

    public static boolean supportsType(ItemStack stack, String type) {
        return supportedTypes(stack).contains(type);
    }

    public static Holder<Attribute> attributeFor(String type) {
        if (SPELL_PENETRATION.equals(type)) {
            return ForbiddenKnowledgeMod.SPELL_PENETRATION;
        }
        ResourceLocation id = ResourceLocation.tryParse(type);
        return id == null ? null : BuiltInRegistries.ATTRIBUTE.getHolder(id).orElse(null);
    }

    public static String attributeNameKey(String type) {
        return switch (type) {
            case FIRE -> "tooltip.forbidden_knowledge.attribute.fire";
            case ARCANE -> "tooltip.forbidden_knowledge.attribute.arcane";
            case FROST -> "tooltip.forbidden_knowledge.attribute.frost";
            case RANGED_DAMAGE -> "tooltip.forbidden_knowledge.attribute.ranged_damage";
            case HEALING -> "tooltip.forbidden_knowledge.attribute.healing";
            case SPELL_PENETRATION -> "tooltip.forbidden_knowledge.attribute.spell_penetration";
            default -> type;
        };
    }

    public static String formatValue(String type, double value) {
        if (SPELL_PENETRATION.equals(type)) {
            return String.format(Locale.ROOT, "%.1f%%", value * 100.0D);
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    public static boolean isStaff(ItemStack stack) {
        return STAFF_ITEM_CLASS != null && STAFF_ITEM_CLASS.isInstance(stack.getItem());
    }

    private static Class<?> resolveStaffItemClass() {
        try {
            return Class.forName("net.spell_engine.api.item.weapon.StaffItem", false,
                    ForbiddenEnhancement.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            return null;
        }
    }

    private static double remainingCapacity(String type, List<ForbiddenAttributeData.Enhancement> enhancements) {
        double current = 0.0D;
        for (ForbiddenAttributeData.Enhancement enhancement : enhancements) {
            if (enhancement.type().equals(type)) {
                current = enhancement.value();
                break;
            }
        }
        return Math.max(0.0D, AaTu_EXUp_ModConfig.forbiddenKnowledgeAttributeLimit(type) - current);
    }

    private static ResourceLocation modifierId(ItemStack stack, String type) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String itemPath = itemId == null ? "unknown_item" : itemId.getNamespace() + "_" + itemId.getPath();
        String typePath = type.replace(':', '_').replace('/', '_');
        return ForbiddenKnowledgeMod.id("enhanced_" + itemPath + "_" + typePath);
    }

    private static long seed(ItemStack stack) {
        long seed = Integer.toUnsignedLong(ItemStack.hashItemAndComponents(stack));
        seed ^= 0x9E3779B97F4A7C15L;
        seed = (seed ^ (seed >>> 30)) * 0xBF58476D1CE4E5B9L;
        seed = (seed ^ (seed >>> 27)) * 0x94D049BB133111EBL;
        return seed ^ (seed >>> 31);
    }
}
