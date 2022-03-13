package com.rocketnotfound.rnf.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nullable;

public class EnchantNGParticle extends TextureSheetParticle {
    protected final double xStart;
    protected final double yStart;
    protected final double zStart;

    protected EnchantNGParticle(ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(world, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.xStart = x;
        this.yStart = y;
        this.zStart = z;
        this.xo = x + xSpeed;
        this.yo = y + ySpeed;
        this.zo = z + zSpeed;
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        float colorRand = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 0.9F * colorRand;
        this.gCol = 0.9F * colorRand;
        this.bCol = colorRand;
        this.hasPhysics = false;
        this.lifetime = (int)(Math.random() * 10.0D) + 30;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void move(double $$0, double $$1, double $$2) {
        this.setBoundingBox(this.getBoundingBox().move($$0, $$1, $$2));
        this.setLocationFromBoundingbox();
    }

    public int getLightColor(float $$0) {
        int $$1 = super.getLightColor($$0);
        float $$2 = (float)this.age / (float)this.lifetime;
        $$2 *= $$2;
        $$2 *= $$2;
        int $$3 = $$1 & 255;
        int $$4 = $$1 >> 16 & 255;
        $$4 += (int)($$2 * 15.0F * 16.0F);
        if ($$4 > 240) {
            $$4 = 240;
        }

        return $$3 | $$4 << 16;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float $$0 = (float)this.age / (float)this.lifetime;
            $$0 = 1.0F - $$0;
            this.x = this.xStart + this.xd * (double)$$0;
            this.y = this.yStart + this.yd * (double)$$0;
            this.z = this.zStart + this.zd * (double)$$0;
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType var1, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            EnchantNGParticle particle = new EnchantNGParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprite);
            return particle;
        }

    }
}
