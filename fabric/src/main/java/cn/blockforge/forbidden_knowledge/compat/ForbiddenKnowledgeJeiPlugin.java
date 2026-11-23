package cn.blockforge.forbidden_knowledge.compat;

import cn.blockforge.forbidden_knowledge.ForbiddenKnowledgeMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Makes the drop-only Forbidden Knowledge book searchable in JEI. */
@JeiPlugin
public final class ForbiddenKnowledgeJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ForbiddenKnowledgeMod.id("jei");
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(List.of(new ItemStack(ForbiddenKnowledgeMod.FORBIDDEN_KNOWLEDGE)));
    }
}
