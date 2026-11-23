package cn.blockforge.forbidden_knowledge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ForbiddenKnowledgeMod.MOD_ID)
public final class ForbiddenKnowledgeMod {
    public static final String MOD_ID = "forbidden_knowledge";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MOD_ID);
    public static final DeferredRegister RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MOD_ID);

    public static final DeferredHolder<net.minecraft.world.entity.ai.attributes.Attribute,
            net.minecraft.world.entity.ai.attributes.Attribute> SPELL_PENETRATION =
            ATTRIBUTES.register("spell_penetration", () -> new net.minecraft.world.entity.ai.attributes.RangedAttribute(
                    "attribute.name.forbidden_knowledge.spell_penetration", 0.0D, 0.0D, 1.0D)
                    .setSyncable(true));
    public static final DeferredItem<Item> FORBIDDEN_KNOWLEDGE = ITEMS.register(
            "forbidden_knowledge", () -> new ForbiddenKnowledgeBookItem(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer,
            net.minecraft.world.item.crafting.RecipeSerializer> ENHANCEMENT_SERIALIZER =
            RECIPE_SERIALIZERS.register("forbidden_enhancement", ForbiddenEnhancementRecipe.Serializer::new);

    public ForbiddenKnowledgeMod(IEventBus modBus, ModContainer modContainer) {
        ITEMS.register(modBus);
        ATTRIBUTES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        ForbiddenKnowledgeEvents.register(modBus);
    }
}
