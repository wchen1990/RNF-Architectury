package com.rocketnotfound.rnf.mixin.fluid;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.util.client.MixinBackgroundRendererForFluidsHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidState.class)
public abstract class MixinFluidStateForceAllFluidsInWater {
    @Inject(method = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/TagKey;)Z", at = @At("RETURN"), cancellable = true)
    public void rnf_isIn(TagKey<Fluid> tagKey, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && tagKey.equals(FluidTags.WATER) && RNF.serverConfig().MISC.FORCE_UNTAGGED_AS_WATER) {
            cir.setReturnValue(MixinBackgroundRendererForFluidsHelper.matchesCondition(((FluidState)(Object)this)));
        }
    }
}
