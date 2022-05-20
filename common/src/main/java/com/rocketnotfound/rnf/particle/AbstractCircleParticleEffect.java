package com.rocketnotfound.rnf.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public abstract class AbstractCircleParticleEffect implements ParticleEffect {
    protected final Vec3f radii;
    protected final Vec3f radiiDelta;
    protected final float rotationSpeed;
    protected final float rampTime;
    protected final boolean swapXZ;
    protected final int maxAge;

    public AbstractCircleParticleEffect(Vec3f radii, Vec3f radiiDelta, float rotationSpeed, float rampTime, boolean swapXZ, int maxAge) {
        this.radii = radii;
        this.radiiDelta = radiiDelta;
        this.rotationSpeed = rotationSpeed;
        this.rampTime = rampTime;
        this.swapXZ = swapXZ;
        this.maxAge = maxAge;
    }

    public static Vec3f readRadii(StringReader stringReader) throws CommandSyntaxException {
        stringReader.expect(' ');
        float f = stringReader.readFloat();
        stringReader.expect(' ');
        float g = stringReader.readFloat();
        stringReader.expect(' ');
        float h = stringReader.readFloat();
        return new Vec3f(f, g, h);
    }

    public static Vec3f readRadii(PacketByteBuf packetByteBuf) {
        return new Vec3f(packetByteBuf.readFloat(), packetByteBuf.readFloat(), packetByteBuf.readFloat());
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeFloat(this.radii.getX());
        packetByteBuf.writeFloat(this.radii.getY());
        packetByteBuf.writeFloat(this.radii.getZ());
        packetByteBuf.writeFloat(this.radiiDelta.getX());
        packetByteBuf.writeFloat(this.radiiDelta.getY());
        packetByteBuf.writeFloat(this.radiiDelta.getZ());
        packetByteBuf.writeFloat(this.rotationSpeed);
        packetByteBuf.writeFloat(this.rampTime);
        packetByteBuf.writeBoolean(this.swapXZ);
        packetByteBuf.writeInt(this.maxAge);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f %.2f %s %s",
            Registry.PARTICLE_TYPE.getId(this.getType()),
            this.radii.getX(), this.radii.getY(), this.radii.getZ(),
            this.radiiDelta.getX(), this.radiiDelta.getY(), this.radiiDelta.getZ(),
            this.rotationSpeed,
            this.rampTime,
            this.swapXZ,
            this.maxAge
        );
    }

    public Vec3f getRadii() {
        return this.radii;
    }
    public Vec3f getRadiiDelta() {
        return this.radiiDelta;
    }
    public float getRotationSpeed() {
        return this.rotationSpeed;
    }
    public float getRampTime() {
        return this.rampTime;
    }
    public boolean getSwapXZ() {
        return this.swapXZ;
    }
    public int getMaxAge() {
        return this.maxAge;
    }
}
