package com.rocketnotfound.rnf.data.spells;

import com.rocketnotfound.rnf.util.BlockStateParser;
import com.rocketnotfound.rnf.util.ItemEntityHelper;
import com.rocketnotfound.rnf.util.SpellHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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
        entity.addVelocity(vec.getX(), vec.getY() + (vec.getY() >= 0 ? offsetY + 0.25 : 0), vec.getZ());
        entity.velocityModified = true;
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

    public static final SummonBlockWithLootTableSpell SUMMON_BLOCK = (block, lootTables) -> (world, entity) -> {
        Direction facing = entity.getHorizontalFacing();
        BlockPos pos = entity.getBlockPos().offset(facing);

        baseBreakSpell(pos, 1, world, entity, true);
        BlockStateParser.setBlockState(
            world,
            pos,
            String.format(
                "%s[facing=%s]{LootTable:\"%s\"}",
                block,
                facing.getOpposite().asString(),
                lootTables.get(world.random.nextInt(lootTables.size()))
            )
        );

        return entity;
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

    interface FloatAffectedSpell extends SpellEffectDeserialize {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            float f = nbt.getFloat("value");
            return create(f);
        }

        SpellEffect create(float f);
    }
    interface FloatAffectedSpellRequires extends FloatAffectedSpell, RequiresEntity { }
    interface FloatAffectedSpellNoEntity extends FloatAffectedSpell, DoesNotRequiresEntity { }

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
    interface BlockPosAffectedSpellWithStringListNoEntity extends BlockPosAffectedSpellWithStringList, DoesNotRequiresEntity {}

    interface SummonBlockWithLootTableSpell extends SpellEffectDeserialize, RequiresEntity {
        @Override
        default SpellEffect deserialize(NbtCompound nbt) {
            return create(
                nbt.getString("block"),
                nbt.getList("lootTables", NbtElement.STRING_TYPE).stream().map(
                    (element) -> element.getType() == NbtElement.STRING_TYPE ? element.asString() : element.toString()
                ).collect(Collectors.toList())
            );
        }

        SpellEffect create(String block, List<String> lootTables);
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
