package com.rocketnotfound.rnf.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class EnchantNGReverseParticle extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;

    EnchantNGReverseParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f);
        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;
        this.startX = d + g;
        this.startY = e + h;
        this.startZ = f + i;
        this.prevPosX = d;
        this.prevPosY = e;
        this.prevPosZ = f;
        this.x = this.prevPosX;
        this.y = this.prevPosY;
        this.z = this.prevPosZ;
        this.scale = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        float j = this.random.nextFloat() * 0.6F + 0.4F;
        this.red = 0.9F * j;
        this.green = 0.9F * j;
        this.blue = j;
        this.collidesWithWorld = false;
        this.maxAge = (int)(Math.random() * 10.0D) + 30;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        float f = (float)this.age / (float)this.maxAge;
        f *= f;
        f *= f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k += (int)(f * 15.0F * 16.0F);
        if (k > 240) {
            k = 240;
        }

        return j | k << 16;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            float f = (float)this.age / (float)this.maxAge;
            f = 1.0F - f;
            this.x = this.startX - this.velocityX * (double)f;
            this.y = this.startY - this.velocityY * (double)f;
            this.z = this.startZ - this.velocityZ * (double)f;
        }
    }

    public static class Provider implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Provider(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            EnchantNGReverseParticle particle = new EnchantNGReverseParticle(clientWorld, d, e, f, g, h, i);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
