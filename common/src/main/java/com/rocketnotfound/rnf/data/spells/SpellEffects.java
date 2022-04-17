package com.rocketnotfound.rnf.data.spells;

import com.rocketnotfound.rnf.util.SpellHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SpellEffects {
    public static final Map<String, SpellEffectDeserialize> TYPE_MAP = new HashMap<>();

    public static final VectorAffectedSpell ADD_VELOCITY = (vec) -> (world, entity) -> {
        // Apparently, this won't actually add any velocity unless you're off the ground
        // So, we cancel out any negative Y velocity and add a small amount to the Y axis
        double offsetY = entity.getVelocity().getY() < 0 ? entity.getVelocity().getY() * -1 : 0;
        entity.addVelocity(vec.getX(), vec.getY() + offsetY + 0.25, vec.getZ());
        entity.velocityModified = true;
        return entity;
    };
    public static final BlockPosAffectedSpellWith1F EXPLOSION = (vec, f) -> (world, entity) -> {
        world.createExplosion(null, vec.getX(), vec.getY(), vec.getZ(), f, Explosion.DestructionType.NONE);
        return entity;
    };
    public static final NonAffectedSpell KILL = () -> (world, entity) -> {
        entity.damage(DamageSource.MAGIC, Float.MAX_VALUE);
        return entity;
    };
    public static final StatusEffectSpell GIVE_STATUS = (statusEffect, duration, amplifier) -> (world, entity) -> {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect, (int) duration * (statusEffect.isInstant() ? 1 : 20), (int) amplifier));
        return entity;
    };
    public static final BlockPosAffectedSpell WARP = (vec) -> (world, entity) -> {
        entity.setPosition(Vec3d.of(vec));
        world.getChunkManager().sendToNearbyPlayers(entity, new EntityPositionS2CPacket(entity));
        return entity;
    };
    public static final BlockPosAffectedSpellWithDim WARP_DIM = (vec, dimKey) -> (world, entity) -> {
        MinecraftServer server = world.getServer();
        Optional<RegistryKey<World>> worldRegKey = server
            .getWorldRegistryKeys().stream()
            .filter((regKey) -> regKey.getValue().equals(new Identifier(dimKey)))
            .findFirst();

        final LivingEntity[] returnEntity = new LivingEntity[1];
        worldRegKey.ifPresent((worldKey) -> {
            ServerWorld changedWorld = server.getWorld(worldKey);
            LivingEntity movedEntity = (LivingEntity) entity.moveToWorld(changedWorld);
            movedEntity.setPosition(Vec3d.of(vec));
            changedWorld.getChunkManager().sendToNearbyPlayers(movedEntity, new EntityPositionS2CPacket(movedEntity));
            returnEntity[0] = movedEntity;
        });

        return returnEntity[0] != null ? returnEntity[0] : entity;
    };

    static {
        TYPE_MAP.put("add_velocity", ADD_VELOCITY);
        TYPE_MAP.put("explosion", EXPLOSION);
        TYPE_MAP.put("kill", KILL);
        TYPE_MAP.put("give_status", GIVE_STATUS);
        TYPE_MAP.put("warp", WARP);
        TYPE_MAP.put("warp_dim", WARP_DIM);
    }

    interface SpellEffect {
        LivingEntity cast(ServerWorld world, LivingEntity entity);
    }
    interface SpellEffectDeserialize {
        SpellEffect deserialize(NbtCompound nbt);
    }
    interface NonAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create();
        }

        SpellEffect create();
    }
    interface StatusEffectSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            StatusEffect effect = Registry.STATUS_EFFECT.get(new Identifier(nbt.getString("status")));
            float d = nbt.getFloat("duration");
            float a = nbt.getFloat("amplifier");
            return create(effect, d, a);
        }

        SpellEffect create(StatusEffect statusEffect, float duration, float amplifier);
    }
    interface BlockPosAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create(NbtHelper.toBlockPos(nbt.getCompound("blockPos")));
        }

        SpellEffect create(BlockPos pos);
    }
    interface BlockPosAffectedSpellWith1F extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            float f = nbt.getFloat("value");
            return create(NbtHelper.toBlockPos(nbt.getCompound("blockPos")), f);
        }

        SpellEffect create(BlockPos pos, float f);
    }
    interface BlockPosAffectedSpellWithDim extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            String str = nbt.getString("dimension");
            return create(NbtHelper.toBlockPos(nbt.getCompound("blockPos")), str);
        }

        SpellEffect create(BlockPos pos, String dimKey);
    }
    interface VectorAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create(SpellHelper.vectorFromNbt(nbt.getCompound("vector")));
        }

        SpellEffect create(Vec3d vec);
    }
}
