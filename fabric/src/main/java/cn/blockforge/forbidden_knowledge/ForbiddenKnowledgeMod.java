package cn.blockforge.forbidden_knowledge;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ForbiddenKnowledgeMod implements ModInitializer {
    public static final String MOD_ID = "forbidden_knowledge";

    public static final Holder<Attribute> SPELL_PENETRATION = Registry.registerForHolder(
            BuiltInRegistries.ATTRIBUTE,
            id("spell_penetration"),
            new RangedAttribute("attribute.name.forbidden_knowledge.spell_penetration", 0.0D, 0.0D, 1.0D)
                    .setSyncable(true));
    public static final Item FORBIDDEN_KNOWLEDGE = Registry.register(
            BuiltInRegistries.ITEM,
            id("forbidden_knowledge"),
            new ForbiddenKnowledgeBookItem(new Item.Properties().stacksTo(16)));
    public static final RecipeSerializer<ForbiddenEnhancementRecipe> ENHANCEMENT_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            id("forbidden_enhancement"),
            new ForbiddenEnhancementRecipe.Serializer());

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ForbiddenKnowledgeEvents.register();
    }
}
