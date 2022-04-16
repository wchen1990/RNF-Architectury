package com.rocketnotfound.rnf.data.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.explosion.Explosion;

public interface ISpellEffects {
    VectorAffectedSpell ADD_VELOCITY = (vec) -> (world, entity) -> { entity.addVelocity(vec.getX(), vec.getY(), vec.getZ()); };
    VectorAffectedSpellWith1F EXPLOSION = (vec, x) -> (world, entity) -> {
        world.createExplosion(null, vec.getX(), vec.getY(), vec.getZ(), x, Explosion.DestructionType.NONE);
    };
    ISpellEffects KILL = (world, entity) -> { entity.damage(DamageSource.MAGIC, Float.MAX_VALUE); };
    StatusEffectSpell GIVE_STATUS = (statusEffect, duration, amplifier) -> (world, entity) -> {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect, duration, amplifier));
    };
    VectorAffectedSpell WARP = (vec) -> (world, entity) -> { entity.setPosition(new Vec3d(vec)); };

    void apply(ServerWorld world, LivingEntity entity);

    interface StatusEffectSpell {
        ISpellEffects create(StatusEffect statusEffect, int duration, int amplifier);
    }
    interface VectorAffectedSpell {
        ISpellEffects create(Vec3f vec);
    }
    interface VectorAffectedSpellWithString {
        ISpellEffects create(Vec3f vec);
    }
    interface VectorAffectedSpellWith1F {
        ISpellEffects create(Vec3f vec, float x);
    }
    interface VectorAffectedSpellWith2F {
        ISpellEffects create(Vec3f vec, float x, float y);
    }
    interface VectorAffectedSpellWith3F {
        ISpellEffects create(Vec3f vec, float x, float y, float z);
    }
}
