package com.rocketnotfound.rnf.util;

import com.rocketnotfound.rnf.data.spells.ISpell;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Optional;

public class SpellHelper {
    public static void processNbtForDeserialization(NbtCompound nbt, ServerWorld world, ISpell spell, BlockPos transcriberPosition) {
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

                    if (nbt.contains(target)) {
                        if (target.equals("vector")) {
                            Vec3d vec = vectorFromNbt(nbt.getCompound("vector"));
                            Vec3d modifier = null;

                            if (type.equals("facing")) {
                                modifier = Vec3d.of(facing.getVector());
                            } else if (type.equals("facing_opposite")) {
                                modifier = Vec3d.of(opposite.getVector());
                            }

                            if (modifier != null) {
                                if (operation.equals("add")) {
                                    vec = vec.add(modifier);
                                } else if (operation.equals("subtract")) {
                                    vec = vec.subtract(modifier);
                                } else if (operation.equals("multiply")) {
                                    vec = vec.multiply(modifier);
                                } else if (operation.equals("divide")) {
                                    vec = vec.multiply(1/modifier.getX(), 1/modifier.getY(), 1/modifier.getZ());
                                }
                            }

                            nbt.put("vector", nbtFromVector(vec));
                        } else if (target.equals("value") || target.equals("duration") || target.equals("amplifier")) {
                            float value = nbt.getFloat(target);
                            Optional<Float> modifier = Optional.empty();

                            if (type.equals("length")) {
                                modifier = Optional.of((float) spell.getLength());
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
                                    value = value / mod;
                                }
                            }

                            nbt.putFloat(target, value);
                        }
                    }
                }
            }
        }

        if (nbt.contains("vector")) {
            Vec3d vec = vectorFromNbt(nbt.getCompound("vector"));
            if (nbt.contains("isPosition") && nbt.getBoolean("isPosition")) {
                nbt.put("blockPos", NbtHelper.fromBlockPos(new BlockPos(vec)));
            } else {
                nbt.put("blockPos", NbtHelper.fromBlockPos(transcriberPosition.add(new Vec3i(vec.getX(), vec.getY(), vec.getZ()))));
            }
        }
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
