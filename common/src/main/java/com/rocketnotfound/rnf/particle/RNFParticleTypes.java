package com.rocketnotfound.rnf.particle;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.mixin.access.DefaultParticleTypeAccess;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class RNFParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(RNF.MOD_ID, Registry.PARTICLE_TYPE_KEY);

    public static final RegistrySupplier<DefaultParticleType> ENCHANT_NG = PARTICLES.register("enchant_ng", () -> DefaultParticleTypeAccess.create(false));
    public static final RegistrySupplier<DefaultParticleType> ENCHANT_NG_REV = PARTICLES.register("enchant_ng_rev", () -> DefaultParticleTypeAccess.create(false));
}
