package com.rocketnotfound.rnf.data.spells;

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
        entity.addVelocity(vec.getX(), vec.getY(), vec.getZ());
        entity.velocityModified = true;
    };
    public static final BlockPosAffectedSpellWith1F EXPLOSION = (vec, f) -> (world, entity) -> {
        world.createExplosion(null, vec.getX(), vec.getY(), vec.getZ(), f, Explosion.DestructionType.NONE);
    };
    public static final NonAffectedSpell KILL = () -> (world, entity) -> {
        entity.damage(DamageSource.MAGIC, Float.MAX_VALUE);
    };
    public static final StatusEffectSpell GIVE_STATUS = (statusEffect, duration, amplifier) -> (world, entity) -> {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect, duration, amplifier));
    };
    public static final BlockPosAffectedSpell WARP = (vec) -> (world, entity) -> {
        entity.setPosition(Vec3d.of(vec));
        world.getChunkManager().sendToNearbyPlayers(entity, new EntityPositionS2CPacket(entity));
    };
    public static final BlockPosAffectedSpellWithDim WARP_DIM = (vec, dimKey) -> (world, entity) -> {
        MinecraftServer server = world.getServer();
        Optional<RegistryKey<World>> worldRegKey = server
            .getWorldRegistryKeys().stream()
            .filter((regKey) -> regKey.getValue().equals(new Identifier(dimKey)))
            .findFirst();

        worldRegKey.ifPresent((worldKey) -> {
            entity.moveToWorld(server.getWorld(worldKey));
            entity.setPosition(Vec3d.of(vec));
            world.getChunkManager().sendToNearbyPlayers(entity, new EntityPositionS2CPacket(entity));
        });
    };

    static {
        TYPE_MAP.put("add_velocity", ADD_VELOCITY);
        TYPE_MAP.put("explosion", EXPLOSION);
        TYPE_MAP.put("kill", KILL);
        TYPE_MAP.put("give_status", GIVE_STATUS);
        TYPE_MAP.put("warp", WARP);
        TYPE_MAP.put("warp_dim", WARP_DIM);
    }

    public static Vec3d vectorFromNbt(NbtCompound vector) {
        double x = vector.getDouble("x");
        double y = vector.getDouble("y");
        double z = vector.getDouble("z");
        return new Vec3d(x, y, z);
    }
    public static NbtCompound nbtFromVector(Vec3d vector) {
        NbtCompound nbt = new NbtCompound();
        nbt.putDouble("x", vector.getX());
        nbt.putDouble("y", vector.getY());
        nbt.putDouble("z", vector.getZ());
        return nbt;
    }

    interface SpellEffect {
        void cast(ServerWorld world, LivingEntity entity);
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
            int d = nbt.getInt("duration");
            int a = nbt.getInt("amplifier");
            return create(effect, d, a);
        }

        SpellEffect create(StatusEffect statusEffect, int duration, int amplifier);
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
            return create(vectorFromNbt(nbt.getCompound("vector")));
        }

        SpellEffect create(Vec3d vec);
    }
}
