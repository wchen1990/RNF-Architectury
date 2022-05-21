package com.rocketnotfound.rnf.data.spells;

import com.rocketnotfound.rnf.util.ItemEntityHelper;
import com.rocketnotfound.rnf.util.SpellHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class SpellEffects {
    public static final Map<String, SpellEffectDeserialize> TYPE_MAP = new HashMap<>();

    // Pre-defined spell effects
    public static final VectorAffectedSpellRequires ADD_VELOCITY = (vec) -> (world, entity) -> {
        // Apparently, this won't actually add any velocity unless you're off the ground
        // So, we cancel out any negative Y velocity and add a small amount to the Y axis
        // We should only do this when we're adding >= 0 Y velocity
        double offsetY = entity.getVelocity().getY() < 0 ? entity.getVelocity().getY() * -1 : 0;
        Vec3d modVec = new Vec3d(vec.getX(), vec.getY() + (vec.getY() >= 0 ? offsetY + 0.25 : 0), vec.getZ());
        if (entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity.getId(), entity.getVelocity().add(modVec)));
        } else {
            entity.addVelocity(modVec.getX(), modVec.getY(), modVec.getZ());
            entity.velocityModified = true;
        }

        return entity;
    };

    public static final BlockPosAffectedSpellWith1FNoEntity EXPLOSION = (vec, f) -> (world, entity) -> {
        world.createExplosion(null, vec.getX() + 0.5, vec.getY(), vec.getZ() + 0.5, f, Explosion.DestructionType.NONE);
        return entity;
    };

    public static final BlockPosAffectedSpellNoEntity LIGHTNING = (vec) -> (world, entity) -> {
        Entity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightning.setPosition(Vec3d.of(vec).add(0.5, 0, 0.5));
        world.spawnEntity(lightning);
        return entity;
    };

    public static final FloatAffectedSpellRequires DAMAGE = (damage) -> (world, entity) -> {
        entity.damage(DamageSource.MAGIC, damage);
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
        Vec3d entPos = entity.getPos();
        Vec3d offset = entPos
            .subtract(entPos.floorAlongAxes(EnumSet.of(Axis.X, Axis.Y, Axis.Z)));
        Vec3d toPos = Vec3d.of(vec).add(offset);

        if (entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)entity).teleport(world, toPos.getX(), toPos.getY(), toPos.getZ(), entity.getYaw(), entity.getPitch());
        } else {
            entity.setPosition(toPos);
        }

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
            Vec3d entPos = entity.getPos();
            Vec3d offset = entPos
                .subtract(entPos.floorAlongAxes(EnumSet.of(Axis.X, Axis.Y, Axis.Z)));
            Vec3d toPos = Vec3d.of(vec).add(offset);

            ServerWorld changedWorld = server.getWorld(worldKey);

            if (entity instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity)entity).teleport(changedWorld, toPos.getX(), toPos.getY(), toPos.getZ(), entity.getYaw(), entity.getPitch());
            } else {
                LivingEntity movedEntity = (LivingEntity) entity.getType().create(changedWorld);
                if (movedEntity != null) {
                    movedEntity.copyFrom(entity);
                    movedEntity.refreshPositionAndAngles(toPos.getX(), toPos.getY(), toPos.getZ(), movedEntity.getYaw(), movedEntity.getPitch());
                    movedEntity.setVelocity(movedEntity.getVelocity());
                    changedWorld.onDimensionChanged(movedEntity);
                }

                entity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                ((ServerWorld)entity.world).resetIdleTimeout();
                changedWorld.resetIdleTimeout();

                returnEntity[0] = movedEntity;
            }
        });

        return returnEntity[0] != null ? returnEntity[0] : entity;
    };

    private static void baseBreakSpell(BlockPos blockPos, float f, ServerWorld world, LivingEntity entity, boolean useSilkTouch) {
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (!blockState.isAir() && !(block instanceof FluidBlock) && blockState.getHardness(world, blockPos) != -1) {
            FluidState fluidState = world.getFluidState(blockPos);

            if (!(blockState.getBlock() instanceof AbstractFireBlock)) {
                world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
            }

            LootContext.Builder build = new LootContext.Builder(world)
                .luck(f)
                .random(world.random)
                .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockPos))
                .parameter(LootContextParameters.TOOL, ItemEntityHelper.FORTUNE_SILK_TOOL_HELPER.apply((int)f, useSilkTouch))
                .optionalParameter(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos));
            Block.dropStacks(blockState, build);

            boolean bl2 = world.setBlockState(blockPos, fluidState.getBlockState(), 3, 512);
            if (bl2) {
                world.emitGameEvent(entity, GameEvent.BLOCK_DESTROY, blockPos);
            }
        }
    }
    public static final BlockPosAffectedSpellWith1FNoEntity BREAK = (blockPos, f) -> (world, entity) -> {
        baseBreakSpell(blockPos, f, world, entity, false);
        return entity;
    };
    public static final BlockPosAffectedSpellWith1FNoEntity SILK_BREAK = (blockPos, f) -> (world, entity) -> {
        baseBreakSpell(blockPos, f, world, entity, true);
        return entity;
    };

    public static final SummonBlockSpell SUMMON_BLOCK = (pos, block, additionalNbtValues) -> (world, entity) -> {
        Direction facing = entity.getHorizontalFacing();

        FluidState initFluidState = world.getFluidState(pos);
        baseBreakSpell(pos, 1, world, entity, true);

        BlockState modState = Registry.BLOCK.get(new Identifier(block)).getDefaultState();
        if (modState.getProperties().contains(Properties.HORIZONTAL_FACING)) {
            modState = modState.with(Properties.HORIZONTAL_FACING, facing.getOpposite());
        }

        BlockState blockState = Block.postProcessState(modState, world, pos);
        if (blockState.isAir()) {
            blockState = modState;
        }

        world.setBlockState(pos, blockState, 3);
        if (additionalNbtValues.size() > 0) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.readNbt(additionalNbtValues.get(world.random.nextInt(additionalNbtValues.size())));
            }
        }

        if (!initFluidState.isEmpty()) {
            BlockState newState = world.getBlockState(pos);
            ((FluidFillable) newState.getBlock()).tryFillWithFluid(world, pos, newState, initFluidState);
        }

        return entity;
    };

    public static final SummonEntitySpell SUMMON_ENTITY = (pos, entities, additionalNbtValues) -> (world, triggeringEntity) -> {
        if (entities.size() > 0) {
            Identifier toSummon = new Identifier(entities.get(world.random.nextInt(entities.size())));
            Optional<EntityType<?>> entityType = Registry.ENTITY_TYPE.getOrEmpty(toSummon).filter(EntityType::isSummonable);
            if (entityType.isPresent()) {
                NbtCompound nbtCompound = new NbtCompound();
                nbtCompound.putString("id", toSummon.toString());

                if (additionalNbtValues.size() > 0) {
                    nbtCompound.copyFrom(additionalNbtValues.get(world.random.nextInt(additionalNbtValues.size())));
                }

                Entity entity = EntityType.loadEntityWithPassengers(nbtCompound, world, (entityx) -> {
                    entityx.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entityx.getYaw(), entityx.getPitch());
                    return entityx;
                });

                if (entity instanceof MobEntity) {
                    ((MobEntity)entity).initialize(world, world.getLocalDifficulty(entity.getBlockPos()), SpawnReason.EVENT, (EntityData)null, (NbtCompound)null);
                }
                world.spawnNewEntityAndPassengers(entity);
            }
        }
        return triggeringEntity;
    };

        // Put in defined spell effects into our map
    // Should be able to add and remove effects from this easily
    static {
        TYPE_MAP.put("add_velocity", ADD_VELOCITY);
        TYPE_MAP.put("explosion", EXPLOSION);
        TYPE_MAP.put("lightning", LIGHTNING);
        TYPE_MAP.put("damage", DAMAGE);
        TYPE_MAP.put("kill", KILL);
        TYPE_MAP.put("give_status", GIVE_STATUS);
        TYPE_MAP.put("warp", WARP);
        TYPE_MAP.put("warp_dim", WARP_DIM);
        TYPE_MAP.put("break", BREAK);
        TYPE_MAP.put("silk_break", SILK_BREAK);
        TYPE_MAP.put("summon_block", SUMMON_BLOCK);
        TYPE_MAP.put("summon_entity", SUMMON_ENTITY);
    }

    // Boilerplate interfaces that were defined so that we can _lazily_ define spell effects
    interface SpellEffect {
        LivingEntity cast(ServerWorld world, @Nullable LivingEntity entity);
    }

    interface EntityRequirement {
        boolean requiresEntity();
    }
    interface RequiresEntity extends EntityRequirement {
        @Override
        default boolean requiresEntity() { return true; }
    }
    interface DoesNotRequireEntity extends EntityRequirement {
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
    interface NonAffectedSpellNoEntity extends NonAffectedSpell, DoesNotRequireEntity { }

    interface FloatAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            float f = nbt.getFloat("value");
            return create(f);
        }

        SpellEffect create(float f);
    }
    interface FloatAffectedSpellRequires extends FloatAffectedSpell, RequiresEntity { }
    interface FloatAffectedSpellNoEntity extends FloatAffectedSpell, DoesNotRequireEntity { }

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
    interface BlockPosAffectedSpellNoEntity extends BlockPosAffectedSpell, DoesNotRequireEntity {}

    interface BlockPosAffectedSpellWith1F extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            float f = nbt.getFloat("value");
            return create(NbtHelper.toBlockPos(nbt.getCompound("blockPos")), f);
        }

        SpellEffect create(BlockPos pos, float f);
    }
    interface BlockPosAffectedSpellWith1FRequires extends BlockPosAffectedSpellWith1F, RequiresEntity {}
    interface BlockPosAffectedSpellWith1FNoEntity extends BlockPosAffectedSpellWith1F, DoesNotRequireEntity {}

    interface BlockPosAffectedSpellWithStringList extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create(
                NbtHelper.toBlockPos(nbt.getCompound("blockPos")),
                nbt.getList("values", NbtElement.STRING_TYPE).stream().map(
                    (element) -> element.getType() == NbtElement.STRING_TYPE ? element.asString() : element.toString()
                ).collect(Collectors.toList())
            );
        }

        SpellEffect create(BlockPos pos, List<String> parameters);
    }
    interface BlockPosAffectedSpellWithStringListRequires extends BlockPosAffectedSpellWithStringList, RequiresEntity {}
    interface BlockPosAffectedSpellWithStringListNoEntity extends BlockPosAffectedSpellWithStringList, DoesNotRequireEntity {}

    interface SummonBlockSpell extends SpellEffectDeserialize, RequiresEntity {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            List<NbtCompound> additionalNbt = nbt.contains("additionalNbt") ? nbt.getList("additionalNbt", NbtElement.COMPOUND_TYPE).stream().map(
                (element) -> element.getType() == NbtElement.COMPOUND_TYPE ? (NbtCompound)element : null
            ).collect(Collectors.toList()) : Collections.EMPTY_LIST;
            return create(
                NbtHelper.toBlockPos(nbt.getCompound("blockPos")),
                nbt.getString("block"),
                additionalNbt
            );
        }

        SpellEffect create(BlockPos pos, String block, List<NbtCompound> additionalNbt);
    }

    interface SummonEntitySpell extends SpellEffectDeserialize, DoesNotRequireEntity {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            List<String> entities = nbt.contains("entities") ? nbt.getList("entities", NbtElement.STRING_TYPE).stream().map(
                (element) -> element.getType() == NbtElement.STRING_TYPE ? element.asString() : element.toString()
            ).collect(Collectors.toList()) : Collections.EMPTY_LIST;
            List<NbtCompound> additionalNbt = nbt.contains("additionalNbt") ? nbt.getList("additionalNbt", NbtElement.COMPOUND_TYPE).stream().map(
                (element) -> element.getType() == NbtElement.COMPOUND_TYPE ? (NbtCompound)element : null
            ).collect(Collectors.toList()) : Collections.EMPTY_LIST;
            return create(
                NbtHelper.toBlockPos(nbt.getCompound("blockPos")),
                entities,
                additionalNbt
            );
        }

        SpellEffect create(BlockPos pos, List<String> entities, List<NbtCompound> additionalNbt);
    }

    interface BlockPosAffectedSpellWithDim extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            String str = nbt.getString("dimension");
            return create(NbtHelper.toBlockPos(nbt.getCompound("blockPos")), str);
        }

        SpellEffect create(BlockPos pos, String dimKey);
    }
    interface BlockPosAffectedSpellWithDimRequires extends BlockPosAffectedSpellWithDim, RequiresEntity {}
    interface BlockPosAffectedSpellWithDimNoEntity extends BlockPosAffectedSpellWithDim, DoesNotRequireEntity {}

    interface VectorAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create(SpellHelper.vectorFromNbt(nbt.getCompound("vector")));
        }

        SpellEffect create(Vec3d vec);
    }
    interface VectorAffectedSpellRequires extends VectorAffectedSpell, RequiresEntity {}
    interface VectorAffectedSpellNoEntity extends VectorAffectedSpell, DoesNotRequireEntity {}
}
