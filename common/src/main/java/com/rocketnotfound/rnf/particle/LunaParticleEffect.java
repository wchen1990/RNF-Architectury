package com.rocketnotfound.rnf.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;

public class LunaParticleEffect extends AbstractCircleParticleEffect {
    public static final LunaParticleEffect DEFAULT;
    public static final Codec<LunaParticleEffect> CODEC;
    public static final Factory<LunaParticleEffect> PARAMETERS_FACTORY;

    static {
        DEFAULT = new LunaParticleEffect(Vec3f.ZERO, Vec3f.ZERO, 0f, 0f, false, 30);
        CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                Vec3f.CODEC.fieldOf("radii").forGetter((particleEffect) -> {
                    return particleEffect.radii;
                }),
                Vec3f.CODEC.fieldOf("radiiDelta").forGetter((particleEffect) -> {
                    return particleEffect.radiiDelta;
                }),
                Codec.FLOAT.fieldOf("rotationSpeed").forGetter((particleEffect) -> {
                    return particleEffect.rotationSpeed;
                }),
                Codec.FLOAT.fieldOf("rampTime").forGetter((particleEffect) -> {
                    return particleEffect.rampTime;
                }),
                Codec.BOOL.fieldOf("swapXZ").forGetter((particleEffect) -> {
                    return particleEffect.swapXZ;
                }),
                Codec.INT.fieldOf("maxAge").forGetter((particleEffect) -> {
                    return particleEffect.maxAge;
                })
            ).apply(instance, LunaParticleEffect::new);
        });
        PARAMETERS_FACTORY = new Factory<LunaParticleEffect>() {
            public LunaParticleEffect read(ParticleType<LunaParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException {
                Vec3f radii = AbstractCircleParticleEffect.readRadii(stringReader);
                Vec3f radii1Delta = AbstractCircleParticleEffect.readRadii(stringReader);
                stringReader.expect(' ');
                float rotationSpeed = stringReader.readFloat();
                stringReader.expect(' ');
                float rampTime = stringReader.readFloat();
                stringReader.expect(' ');
                boolean swapXZ = stringReader.readBoolean();
                stringReader.expect(' ');
                int maxAge = stringReader.readInt();
                return new LunaParticleEffect(radii, radii1Delta, rotationSpeed, rampTime, swapXZ, maxAge);
            }

            public LunaParticleEffect read(ParticleType<LunaParticleEffect> particleType, PacketByteBuf packetByteBuf) {
                return new LunaParticleEffect(
                    AbstractCircleParticleEffect.readRadii(packetByteBuf),
                    AbstractCircleParticleEffect.readRadii(packetByteBuf),
                    packetByteBuf.readFloat(),
                    packetByteBuf.readFloat(),
                    packetByteBuf.readBoolean(),
                    packetByteBuf.readInt()
                );
            }
        };
    }

    public LunaParticleEffect(Vec3f radii, Vec3f radiiDelta, float rotationSpeed, float rampTime, boolean swapXZ, int maxAge) {
        super(radii, radiiDelta, rotationSpeed, rampTime, swapXZ, maxAge);
    }

    @Override
    public ParticleType<LunaParticleEffect> getType() {
        return RNFParticleTypes.LUNA_EFFECT.get();
    }
}
