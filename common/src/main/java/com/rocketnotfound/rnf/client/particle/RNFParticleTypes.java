package com.rocketnotfound.rnf.client.particle;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.mixin.access.SimpleParticleTypeAccess;
import com.rocketnotfound.rnf.util.RegistryObject;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class RNFParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(RNF.MOD_ID, Registry.PARTICLE_TYPE_REGISTRY);

    public static final SimpleParticleType ENCHANT_NG = createSimpleParticle(SimpleParticleTypeAccess.create(false), "enchant_ng");

    public static <T extends ParticleType<?>> T createSimpleParticle(T particle, String id) {
        PARTICLES.register(id, () -> particle);
        return particle;
    }
}
