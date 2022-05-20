package com.rocketnotfound.rnf.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class TrigSpiralParticle extends NoGravityParticle {
    protected final float baseTrigDelta = 0.25F;

    protected final float xTrigDelta;
    protected final float yTrigDelta;
    protected final float zTrigDelta;

    TrigSpiralParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        this(clientWorld, d, e, f, g, h, i, false, false, false, 0F);
    }
    TrigSpiralParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse) {
        this(clientWorld, d, e, f, g, h, i, reverse, false, false, 0F);
    }
    TrigSpiralParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright) {
        this(clientWorld, d, e, f, g, h, i, reverse, fullBright, false, 0F);
    }
    TrigSpiralParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright, boolean allWhite) {
        this(clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, 0F);
    }
    TrigSpiralParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright, boolean allWhite, float fixedScale) {
        super(clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, fixedScale);

        this.xTrigDelta = baseTrigDelta * this.random.nextFloat();
        this.yTrigDelta = baseTrigDelta * this.random.nextFloat();
        this.zTrigDelta = baseTrigDelta * this.random.nextFloat();
    }

    @Override
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
            double fPi = f * (8 * Math.PI);
            this.x = this.startX + (this.velocityX * (double)f + this.xTrigDelta * Math.cos(fPi)) * revMul;
            this.y = this.startY + (this.velocityY * (double)f + this.yTrigDelta * Math.sin(fPi)) * revMul;
            this.z = this.startZ + (this.velocityZ * (double)f + this.zTrigDelta * Math.cos(fPi)) * revMul;
        }
    }

    public static class Normal implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Normal(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            TrigSpiralParticle particle = new TrigSpiralParticle(clientWorld, d, e, f, g, h, i, false, true);
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
            TrigSpiralParticle particle = new TrigSpiralParticle(clientWorld, d, e, f, g, h, i, true, true);
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
            TrigSpiralParticle particle = new TrigSpiralParticle(clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, fixedScale);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
