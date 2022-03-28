package com.rocketnotfound.rnf.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class NoGravityParticle extends SpriteBillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;

    private final boolean reverse;
    private final boolean fullBright;
    private final boolean allWhite;
    private final float fixedScale;

    NoGravityParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        this(clientWorld, d, e, f, g, h, i, false, false, false, 0F);
    }
    NoGravityParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse) {
        this(clientWorld, d, e, f, g, h, i, reverse, false, false, 0F);
    }
    NoGravityParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright) {
        this(clientWorld, d, e, f, g, h, i, reverse, fullBright, false, 0F);
    }
    NoGravityParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright, boolean allWhite) {
        this(clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, 0F);
    }
    NoGravityParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright, boolean allWhite, float fixedScale) {
        super(clientWorld, d, e, f);

        this.velocityX = g;
        this.velocityY = h;
        this.velocityZ = i;

        int mul1 = (reverse) ? 1 : 0;
        this.startX = d + (g * mul1);
        this.startY = e + (h * mul1);
        this.startZ = f + (i * mul1);

        int mul2 = (reverse) ? 0 : 1;
        this.prevPosX = d + (g * mul2);
        this.prevPosY = e + (h * mul2);
        this.prevPosZ = f + (i * mul2);

        this.x = this.prevPosX;
        this.y = this.prevPosY;
        this.z = this.prevPosZ;

        this.reverse = reverse;
        this.fullBright = fullBright;
        this.allWhite = allWhite;
        this.fixedScale = fixedScale;

        this.scale = (fixedScale > 0) ? fixedScale : 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);

        if (allWhite) {
            this.red = 1F;
            this.green = 1F;
            this.blue = 1F;
        } else {
            float j = this.random.nextFloat() * 0.6F + 0.4F;
            this.red = 0.9F * j;
            this.green = 0.9F * j;
            this.blue = j;
        }

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
        if (this.fullBright) return 255 | 255 << 16;

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
            int revMul = (this.reverse) ? -1 : 1;
            float f = (float)this.age / (float)this.maxAge;
            f = 1.0F - f;
            this.x = this.startX + (this.velocityX * (double)f) * revMul;
            this.y = this.startY + (this.velocityY * (double)f) * revMul;
            this.z = this.startZ + (this.velocityZ * (double)f) * revMul;
        }
    }

    public static class Normal implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Normal(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            NoGravityParticle particle = new NoGravityParticle(clientWorld, d, e, f, g, h, i, false, true);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

    public static class Reverse implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Reverse(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            NoGravityParticle particle = new NoGravityParticle(clientWorld, d, e, f, g, h, i, true, true);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

    public static class Custom implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        private final boolean reverse;
        private final boolean fullBright;
        private final boolean allWhite;
        private final float fixedScale;

        public Custom(SpriteProvider spriteProvider, boolean reverse, boolean fullBright, boolean allWhite, float fixedScale) {
            this.spriteProvider = spriteProvider;

            this.reverse = reverse;
            this.fullBright = fullBright;
            this.allWhite = allWhite;
            this.fixedScale = fixedScale;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            NoGravityParticle particle = new NoGravityParticle(clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, fixedScale);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
