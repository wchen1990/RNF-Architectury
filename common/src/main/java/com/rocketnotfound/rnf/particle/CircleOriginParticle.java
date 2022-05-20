package com.rocketnotfound.rnf.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class CircleOriginParticle extends NoGravityParticle {
    protected final float xRadius;
    protected final float yRadius;
    protected final float zRadius;

    protected final float xRadiusDelta;
    protected final float yRadiusDelta;
    protected final float zRadiusDelta;

    protected final float rotationSpeed;
    protected final float rampTime;
    protected final boolean swapXZ;

    protected final float initScale;

    CircleOriginParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        this(circleParticleEffect, clientWorld, d, e, f, g, h, i, false, false, false, 0F);
    }
    CircleOriginParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse) {
        this(circleParticleEffect, clientWorld, d, e, f, g, h, i, reverse, false, false, 0F);
    }
    CircleOriginParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright) {
        this(circleParticleEffect, clientWorld, d, e, f, g, h, i, reverse, fullBright, false, 0F);
    }
    CircleOriginParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright, boolean allWhite) {
        this(circleParticleEffect, clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, 0F);
    }
    CircleOriginParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, boolean reverse, boolean fullBright, boolean allWhite, float fixedScale) {
        super(clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, fixedScale);

        Vec3f radii = circleParticleEffect.getRadii();
        this.xRadius = radii.getX();
        this.yRadius = radii.getY();
        this.zRadius = radii.getZ();

        Vec3f radiiDelta = circleParticleEffect.getRadiiDelta();
        this.xRadiusDelta = radiiDelta.getX();
        this.yRadiusDelta = radiiDelta.getY();
        this.zRadiusDelta = radiiDelta.getZ();

        this.rotationSpeed = circleParticleEffect.getRotationSpeed();
        this.rampTime = circleParticleEffect.getRampTime();
        this.swapXZ = circleParticleEffect.getSwapXZ();

        this.scale = (this.fixedScale == 0F) ? 0.175F : this.scale;
        this.initScale = this.scale;

        this.maxAge = circleParticleEffect.getMaxAge();
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
        } else {
            float f = (float)this.age / (float)this.maxAge;
            float invF = 1.0f - f;
            float ramp = MathHelper.clamp(this.age, 1, rampTime) / Math.max(1, Math.abs(rampTime));
            double fPi = invF * (this.rotationSpeed * Math.PI);

            if (this.fixedScale == 0F) {
                this.scale = this.initScale * invF;
            }

            double sin = Math.sin(fPi);
            double cos = Math.cos(fPi);
            this.x = this.startX + ((this.xRadius + this.xRadiusDelta * f) * (this.swapXZ ? sin : cos)) * ramp;
            this.y = this.startY + ((this.yRadius + this.yRadiusDelta * f) * cos) * ramp;
            this.z = this.startZ + ((this.zRadius + this.zRadiusDelta * f) * (this.swapXZ ? cos : sin)) * ramp;
        }
    }

    public static class Normal implements ParticleFactory<AbstractCircleParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Normal(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            CircleOriginParticle particle = new CircleOriginParticle(circleParticleEffect, clientWorld, d, e, f, g, h, i, false, true);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

    public static class Reverse implements ParticleFactory<AbstractCircleParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Reverse(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            CircleOriginParticle particle = new CircleOriginParticle(circleParticleEffect, clientWorld, d, e, f, g, h, i, true, true);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }

    public static class Custom implements ParticleFactory<AbstractCircleParticleEffect> {
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

        public Particle createParticle(AbstractCircleParticleEffect circleParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            CircleOriginParticle particle = new CircleOriginParticle(circleParticleEffect, clientWorld, d, e, f, g, h, i, reverse, fullBright, allWhite, fixedScale);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}

