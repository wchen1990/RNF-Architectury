package com.rocketnotfound.rnf.particle;

import com.mojang.serialization.Codec;
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

    public static final RegistrySupplier<DefaultParticleType> END_ROD = PARTICLES.register("end_rod", () -> DefaultParticleTypeAccess.create(false));
    public static final RegistrySupplier<DefaultParticleType> END_ROD_REV = PARTICLES.register("end_rod_rev", () -> DefaultParticleTypeAccess.create(false));

    public static final RegistrySupplier<ParticleType<LunaParticleEffect>> LUNA_EFFECT = PARTICLES.register("luna_effect", () -> new ParticleType<LunaParticleEffect>(false, LunaParticleEffect.PARAMETERS_FACTORY) {
        @Override
        public Codec<LunaParticleEffect> getCodec() {
            return LunaParticleEffect.CODEC;
        }
    });
}
