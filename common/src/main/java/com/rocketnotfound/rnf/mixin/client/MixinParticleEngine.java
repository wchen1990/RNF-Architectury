package com.rocketnotfound.rnf.mixin.client;

import com.rocketnotfound.rnf.client.particle.EnchantNGParticle;
import com.rocketnotfound.rnf.client.particle.RNFParticleTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(ParticleEngine.class)
public class MixinParticleEngine {

    @Shadow
    private <T extends ParticleOptions> void register(ParticleType<? extends T> particle, ParticleEngine.SpriteParticleRegistration<T> provider) {
        throw new Error("Mixin did not apply!");
    }

    @Inject(method = ("registerProviders"), at = @At("RETURN"))
    private void registerRNFProviders(CallbackInfo ci) {
        register(RNFParticleTypes.ENCHANT_NG, EnchantNGParticle.Provider::new);
    }
}