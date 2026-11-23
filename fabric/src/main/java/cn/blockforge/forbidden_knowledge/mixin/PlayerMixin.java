package cn.blockforge.forbidden_knowledge.mixin;

import cn.blockforge.forbidden_knowledge.ForbiddenKnowledgeMod;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerMixin {
    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void forbidden_knowledge$addSpellPenetrationAttribute(
            CallbackInfoReturnable<AttributeSupplier.Builder> callbackInfo) {
        callbackInfo.getReturnValue().add(ForbiddenKnowledgeMod.SPELL_PENETRATION, 0.0D);
    }
}

