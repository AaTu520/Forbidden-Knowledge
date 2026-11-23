package cn.blockforge.forbidden_knowledge;

import cn.blockforge.aatu_exup.AaTu_EXUp_ModConfig;
import cn.blockforge.aatu_exup.AttributeData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public final class ForbiddenEnhancementRecipe implements SmithingRecipe {
    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        ItemStack base = input.base();
        if (!input.template().is(ForbiddenKnowledgeMod.FORBIDDEN_KNOWLEDGE.get())
                || !input.addition().is(AaTu_EXUp_ModConfig.forbiddenKnowledgeEnhancementCostItem())
                || !ForbiddenEnhancement.isUpgradeable(base)) {
            return false;
        }
        return (!requiresSoul(base) || AttributeData.canEnhance(base))
                && ForbiddenEnhancement.hasAvailableAttribute(base,
                        ForbiddenAttributeData.getEnhancements(base));
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack base = input.base();
        List<ForbiddenAttributeData.Enhancement> enhancements = ForbiddenAttributeData.getEnhancements(base);
        ForbiddenEnhancement.Roll roll = ForbiddenEnhancement.nextRoll(base, enhancements);
        if (roll == null) {
            return ItemStack.EMPTY;
        }

        ItemStack result = base.copy();
        if (ForbiddenAttributeData.add(result, roll.type(), roll.value()) <= 0.0D) {
            return ItemStack.EMPTY;
        }
        if (requiresSoul(result) && !AttributeData.consumeSoul(result, 1)) {
            return ItemStack.EMPTY;
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 1;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.is(ForbiddenKnowledgeMod.FORBIDDEN_KNOWLEDGE.get());
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return ForbiddenEnhancement.isUpgradeable(stack)
                && (!requiresSoul(stack) || AttributeData.canEnhance(stack));
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.is(AaTu_EXUp_ModConfig.forbiddenKnowledgeEnhancementCostItem());
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(ForbiddenKnowledgeMod.FORBIDDEN_KNOWLEDGE.get()),
                Ingredient.of(AaTu_EXUp_ModConfig.forbiddenKnowledgeEnhancementCostItem()));
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ForbiddenKnowledgeMod.ENHANCEMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    private static boolean requiresSoul(ItemStack stack) {
        return ForbiddenEnhancement.isStaff(stack)
                || stack.getItem() instanceof BowItem
                || stack.getItem() instanceof CrossbowItem;
    }

    public static final class Serializer implements RecipeSerializer<ForbiddenEnhancementRecipe> {
        private static final MapCodec<ForbiddenEnhancementRecipe> CODEC =
                MapCodec.unit(ForbiddenEnhancementRecipe::new);
        private static final StreamCodec<RegistryFriendlyByteBuf, ForbiddenEnhancementRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::encode, Serializer::decode);

        @Override
        public MapCodec<ForbiddenEnhancementRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ForbiddenEnhancementRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static void encode(RegistryFriendlyByteBuf buffer, ForbiddenEnhancementRecipe recipe) {
        }

        private static ForbiddenEnhancementRecipe decode(RegistryFriendlyByteBuf buffer) {
            return new ForbiddenEnhancementRecipe();
        }
    }
}
