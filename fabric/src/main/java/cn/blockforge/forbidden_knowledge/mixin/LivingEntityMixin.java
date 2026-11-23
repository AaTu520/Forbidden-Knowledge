package cn.blockforge.forbidden_knowledge.mixin;

import cn.blockforge.forbidden_knowledge.SpellPenetration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Inject(
            method = "getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F",
            at = @At("RETURN"),
            cancellable = true)
    private void forbidden_knowledge$applySpellPenetration(
            DamageSource source, float amount, CallbackInfoReturnable<Float> callbackInfo) {
        float extraDamage = SpellPenetration.additionalDamage((LivingEntity) (Object) this, source);
        if (extraDamage > 0.0F) {
            callbackInfo.setReturnValue(callbackInfo.getReturnValue() + extraDamage);
        }
    }
}
