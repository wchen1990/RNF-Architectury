package com.rocketnotfound.rnf.mixin.access;

import net.minecraft.particle.DefaultParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DefaultParticleType.class)
public interface DefaultParticleTypeAccess {
    @Invoker("<init>")
    static DefaultParticleType create(boolean glow) {
        throw new Error("Mixin did not apply!");
    }
}