package com.rocketnotfound.rnf.mixin.client;

import com.rocketnotfound.rnf.particle.NoGravityParticle;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.particle.TrigSpiralParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Shadow
    private <T extends ParticleEffect> void registerFactory(ParticleType<? extends T> type, ParticleManager.SpriteAwareFactory<T> factory) {
        throw new Error("Mixin did not apply!");
    }

    @Inject(method = ("registerDefaultFactories"), at = @At("RETURN"))
    private void registerRNFProviders(CallbackInfo ci) {
        registerFactory(RNFParticleTypes.ENCHANT_NG.get(), NoGravityParticle.Normal::new);
        registerFactory(RNFParticleTypes.ENCHANT_NG_REV.get(), NoGravityParticle.Reverse::new);

        registerFactory(RNFParticleTypes.END_ROD.get(), (spriteProvider) -> new TrigSpiralParticle.Custom(spriteProvider, false, true, true, 0F));
        registerFactory(RNFParticleTypes.END_ROD_REV.get(), (spriteProvider) -> new TrigSpiralParticle.Custom(spriteProvider, true, true, true, 0F));
    }
}