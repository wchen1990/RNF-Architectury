package com.rocketnotfound.rnf.mixin.fluid;

import com.rocketnotfound.rnf.RNF;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidState.class)
public abstract class MixinFluidStateForceAllFluidsInWater {
    @Shadow
    public abstract Fluid getFluid();

    @Inject(method = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/TagKey;)Z", at = @At("RETURN"), cancellable = true)
    public void rnf_isIn(TagKey<Fluid> tagKey, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && RNF.serverConfig().MISC.FORCE_FAKE_WATER_FLUID_TAG) {
            FluidState fluidState = getFluid().getDefaultState();
            cir.setReturnValue(tagKey.equals(FluidTags.WATER) && !fluidState.isEmpty() && !fluidState.getFluid().getRegistryEntry().isIn(FluidTags.WATER) && !fluidState.getFluid().getRegistryEntry().isIn(FluidTags.LAVA));
        }
    }
}
