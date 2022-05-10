package com.rocketnotfound.rnf.mixin.access;

import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructureFeature.class)
public class StructureFeatureAccess {
    @Invoker
    public static <F extends StructureFeature<?>> F callRegister(String string, F structureFeature, GenerationStep.Feature feature) {
        throw new UnsupportedOperationException();
    }
}
