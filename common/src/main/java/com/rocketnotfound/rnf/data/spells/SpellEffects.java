package com.rocketnotfound.rnf.data.spells;

import com.rocketnotfound.rnf.util.SpellHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
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

    // Pre-defined spell effects
    public static final VectorAffectedSpellRequires ADD_VELOCITY = (vec) -> (world, entity) -> {
        // Apparently, this won't actually add any velocity unless you're off the ground
        // So, we cancel out any negative Y velocity and add a small amount to the Y axis
        // We should only do this when we're adding >= 0 Y velocity
        double offsetY = entity.getVelocity().getY() < 0 ? entity.getVelocity().getY() * -1 : 0;
        entity.addVelocity(vec.getX(), vec.getY() + (vec.getY() >= 0 ? offsetY + 0.25 : 0), vec.getZ());
        entity.velocityModified = true;
        return entity;
    };
    public static final BlockPosAffectedSpellWith1FNoEntity EXPLOSION = (vec, f) -> (world, entity) -> {
        world.createExplosion(null, vec.getX(), vec.getY(), vec.getZ(), f, Explosion.DestructionType.NONE);
        return entity;
    };
    public static final BlockPosAffectedSpellNoEntity LIGHTNING = (vec) -> (world, entity) -> {
        Entity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setPosition(Vec3d.of(vec));
        world.spawnEntity(lightning);
        return entity;
    };
    public static final NonAffectedSpellRequires KILL = () -> (world, entity) -> {
        entity.damage(DamageSource.MAGIC, Float.MAX_VALUE);
        return entity;
    };
    public static final StatusEffectSpell GIVE_STATUS = (statusEffect, duration, amplifier) -> (world, entity) -> {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect, (int) duration * (statusEffect.isInstant() ? 1 : 20), (int) amplifier));
        return entity;
    };
    public static final BlockPosAffectedSpellRequires WARP = (vec) -> (world, entity) -> {
        entity.setPosition(Vec3d.of(vec));
        world.getChunkManager().sendToNearbyPlayers(entity, new EntityPositionS2CPacket(entity));
        return entity;
    };
    public static final BlockPosAffectedSpellWithDimRequires WARP_DIM = (vec, dimKey) -> (world, entity) -> {
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

    // Put in defined spell effects into our map
    // Should be able to add and remove effects from this easily
    static {
        TYPE_MAP.put("add_velocity", ADD_VELOCITY);
        TYPE_MAP.put("explosion", EXPLOSION);
        TYPE_MAP.put("lightning", LIGHTNING);
        TYPE_MAP.put("kill", KILL);
        TYPE_MAP.put("give_status", GIVE_STATUS);
        TYPE_MAP.put("warp", WARP);
        TYPE_MAP.put("warp_dim", WARP_DIM);
    }

    // Boilerplate interfaces that were defined so that we can _lazily_ define spell effects
    interface SpellEffect {
        LivingEntity cast(ServerWorld world, LivingEntity entity);
    }

    interface EntityRequirement {
        boolean requiresEntity();
    }
    interface RequiresEntity extends EntityRequirement {
        @Override
        default boolean requiresEntity() { return true; }
    }
    interface DoesNotRequiresEntity extends EntityRequirement {
        @Override
        default boolean requiresEntity() { return false; }
    }

    interface SpellEffectDeserialize extends EntityRequirement {
        SpellEffect deserialize(NbtCompound nbt);
    }

    interface NonAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create();
        }

        SpellEffect create();
    }
    interface NonAffectedSpellRequires extends NonAffectedSpell, RequiresEntity { }
    interface NonAffectedSpellNoEntity extends NonAffectedSpell, DoesNotRequiresEntity { }

    interface StatusEffectSpell extends SpellEffectDeserialize, RequiresEntity {
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
    interface BlockPosAffectedSpellRequires extends BlockPosAffectedSpell, RequiresEntity {}
    interface BlockPosAffectedSpellNoEntity extends BlockPosAffectedSpell, DoesNotRequiresEntity {}

    interface BlockPosAffectedSpellWith1F extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            float f = nbt.getFloat("value");
            return create(NbtHelper.toBlockPos(nbt.getCompound("blockPos")), f);
        }

        SpellEffect create(BlockPos pos, float f);
    }
    interface BlockPosAffectedSpellWith1FRequires extends BlockPosAffectedSpellWith1F, RequiresEntity {}
    interface BlockPosAffectedSpellWith1FNoEntity extends BlockPosAffectedSpellWith1F, DoesNotRequiresEntity {}

    interface BlockPosAffectedSpellWithDim extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            String str = nbt.getString("dimension");
            return create(NbtHelper.toBlockPos(nbt.getCompound("blockPos")), str);
        }

        SpellEffect create(BlockPos pos, String dimKey);
    }
    interface BlockPosAffectedSpellWithDimRequires extends BlockPosAffectedSpellWithDim, RequiresEntity {}
    interface BlockPosAffectedSpellWithDimNoEntity extends BlockPosAffectedSpellWithDim, DoesNotRequiresEntity {}

    interface VectorAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create(SpellHelper.vectorFromNbt(nbt.getCompound("vector")));
        }

        SpellEffect create(Vec3d vec);
    }
    interface VectorAffectedSpellRequires extends VectorAffectedSpell, RequiresEntity {}
    interface VectorAffectedSpellNoEntity extends VectorAffectedSpell, DoesNotRequiresEntity {}
}
