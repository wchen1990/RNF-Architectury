package com.rocketnotfound.rnf.util;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RitualPrimerBlockEntity;
import com.rocketnotfound.rnf.data.spells.ISpell;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SpellHelper {
    public static boolean processNbtForDeserialization(NbtCompound nbt, ServerWorld world, ISpell spell, BlockPos transcriberPosition) {
        boolean checkPos = false;
        boolean checkCollision = false;
        boolean isCollidable = false;
        boolean usePrimer = false;

        Optional<Block> checkPosBlock = Optional.empty();

        Direction facing = world.getBlockState(transcriberPosition).get(Properties.FACING);
        Direction opposite = world.getBlockState(transcriberPosition).get(Properties.FACING).getOpposite();

        if (nbt.contains("affectedBy")) {
            NbtList affectedBy = nbt.getList("affectedBy", NbtCompound.COMPOUND_TYPE);
            for (NbtElement affect : affectedBy) {
                if (affect.getType() == NbtCompound.COMPOUND_TYPE) {
                    NbtCompound compoundAffect = (NbtCompound) affect;
                    String type = compoundAffect.getString("type");
                    String target = compoundAffect.getString("target");
                    String operation = compoundAffect.getString("operation");

                    if (type.equals("use_primer")) {
                        usePrimer = true;
                        continue;
                    }

                    if (type.equals("check_pos")) {
                        checkPos = true;

                        if (compoundAffect.contains("block")) {
                            checkPosBlock = Registry.BLOCK.getOrEmpty(new Identifier(compoundAffect.getString("block")));
                        }

                        if (compoundAffect.contains("isCollidable")) {
                            checkCollision = true;
                            isCollidable = compoundAffect.getBoolean("isCollidable");
                        }

                        continue;
                    }

                    if (nbt.contains(target)) {
                        if (target.equals("vector")) {
                            Vec3d vec = vectorFromNbt(nbt.getCompound("vector"));
                            Vec3d modifier = null;

                            if (type.equals("facing")) {
                                modifier = Vec3d.of(facing.getVector());
                            } else if (type.equals("facing_opposite")) {
                                modifier = Vec3d.of(opposite.getVector());
                            } else if (type.equals("num_blocks")) {
                                int radius = compoundAffect.getInt("searchRadius");
                                Optional<Block> searchBlock = Registry.BLOCK.getOrEmpty(new Identifier(compoundAffect.getString("block")));
                                if (searchBlock.isPresent()) {
                                    int numBlocks = (int) world.getStatesInBox(new Box(transcriberPosition).expand(radius))
                                        .filter((blockState) -> blockState.isOf(searchBlock.get()))
                                        .count();
                                    modifier = new Vec3d(numBlocks, numBlocks, numBlocks);
                                }
                            }

                            if (modifier != null) {
                                if (compoundAffect.contains("fields")) {
                                    NbtList fields = compoundAffect.getList("fields", NbtCompound.STRING_TYPE);
                                    Set fieldSet = fields.stream().map((field) -> field.asString()).collect(Collectors.toSet());
                                    modifier = modifier.multiply(
                                        fieldSet.contains("x") ? 1 : 0,
                                        fieldSet.contains("y") ? 1 : 0,
                                        fieldSet.contains("z") ? 1 : 0
                                    );
                                }

                                if (operation.equals("add")) {
                                    vec = vec.add(modifier);
                                } else if (operation.equals("subtract")) {
                                    vec = vec.subtract(modifier);
                                } else if (operation.equals("multiply")) {
                                    vec = vec.multiply(modifier);
                                } else if (operation.equals("divide")) {
                                    double modX = modifier.getX() != 0 ? 1 / modifier.getX() : 1;
                                    double modY = modifier.getY() != 0 ? 1 / modifier.getY() : 1;
                                    double modZ = modifier.getZ() != 0 ? 1 / modifier.getZ() : 1;
                                    vec = vec.multiply(modX, modY, modZ);
                                }
                            }

                            nbt.put("vector", nbtFromVector(vec));
                        } else if (target.equals("value") || target.equals("duration") || target.equals("amplifier")) {
                            float value = nbt.getFloat(target);
                            Optional<Float> modifier = Optional.empty();

                            if (type.equals("length")) {
                                modifier = Optional.of((float) spell.getLength());
                            } else if (type.equals("num_blocks")) {
                                int radius = compoundAffect.getInt("searchRadius");
                                Optional<Block> searchBlock = Registry.BLOCK.getOrEmpty(new Identifier(compoundAffect.getString("block")));
                                if (searchBlock.isPresent()) {
                                    float numBlocks = (float) world.getStatesInBox(new Box(transcriberPosition).expand(radius))
                                        .filter((blockState) -> blockState.isOf(searchBlock.get()))
                                        .count();
                                    modifier = Optional.of(numBlocks);
                                }
                            }

                            if (modifier.isPresent()) {
                                float mod = modifier.get();
                                if (operation.equals("add")) {
                                    value = value + mod;
                                } else if (operation.equals("subtract")) {
                                    value = value - mod;
                                } else if (operation.equals("multiply")) {
                                    value = value * mod;
                                } else if (operation.equals("divide")) {
                                    if (mod != 0) {
                                        value = value / mod;
                                    }
                                }
                            }

                            nbt.putFloat(target, value);
                        }
                    }
                }
            }
        }

        BlockPos positionToUse = transcriberPosition;
        if (usePrimer) {
            if (world.getBlockState(transcriberPosition.offset(opposite)).isOf(RNFBlocks.RITUAL_PRIMER.get())) {
                BlockEntity be = world.getBlockEntity(transcriberPosition.offset(opposite));
                if (be instanceof RitualPrimerBlockEntity) {
                    Pair<Optional<String>, Optional<BlockPos>> info = ((RitualPrimerBlockEntity) be).getTargetInfo();
                    if (info.getLeft().isPresent()) {
                        nbt.putString("dimension", info.getLeft().get());
                    }
                    if (info.getRight().isPresent()) {
                        positionToUse = info.getRight().get();
                    }
                }
            }
        }

        if (nbt.contains("vector")) {
            Vec3d vec = vectorFromNbt(nbt.getCompound("vector"));
            if (nbt.contains("isPosition") && nbt.getBoolean("isPosition")) {
                nbt.put("blockPos", NbtHelper.fromBlockPos(new BlockPos(vec)));
            } else {
                nbt.put("blockPos", NbtHelper.fromBlockPos(positionToUse.add(new Vec3i(vec.getX(), vec.getY(), vec.getZ()))));
            }
        }

        if (usePrimer && !nbt.contains("blockPos")) {
            nbt.put("blockPos", NbtHelper.fromBlockPos(positionToUse));
        }

        if (checkPos && nbt.contains("blockPos")) {
            ServerWorld worldToCheckIn;
            if (nbt.contains("dimension")) {
                MinecraftServer server = world.getServer();
                Optional<RegistryKey<World>> worldRegKey = server
                    .getWorldRegistryKeys().stream()
                    .filter((regKey) -> regKey.getValue().equals(new Identifier(nbt.getString("dimension"))))
                    .findFirst();

                if (worldRegKey.isPresent()) {
                    worldToCheckIn = world.getServer().getWorld(worldRegKey.get());
                } else {
                    return false;
                }
            } else {
                worldToCheckIn = world;
            }

            boolean blockChecks = true;
            BlockPos posToCheck = NbtHelper.toBlockPos(nbt.getCompound("blockPos"));
            BlockState stateToCheck = worldToCheckIn.getBlockState(posToCheck);
            if (checkPosBlock.isPresent()) {
                blockChecks = blockChecks && stateToCheck.isOf(checkPosBlock.get());
            }

            if (checkCollision) {
                if (isCollidable) {
                    blockChecks = blockChecks && stateToCheck.getCollisionShape(world, posToCheck) != VoxelShapes.empty();
                } else {
                    blockChecks = blockChecks && stateToCheck.getCollisionShape(world, posToCheck) == VoxelShapes.empty();
                }
            }

             return blockChecks;
        }

        return true;
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
}
