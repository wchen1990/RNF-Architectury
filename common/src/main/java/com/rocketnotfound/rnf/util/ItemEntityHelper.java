package com.rocketnotfound.rnf.util;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ItemEntityHelper {
    public static void spawnItem(ServerWorld world, BlockPos blockPos, ItemStack itemStack) {
        spawnItem(world, Vec3d.of(blockPos), itemStack, new Vec3d(0,0,0));
    }
    public static void spawnItem(ServerWorld world, Vec3d blockPos, ItemStack itemStack) {
        spawnItem(world, blockPos, itemStack, new Vec3d(0,0,0));
    }
    public static void spawnItem(ServerWorld world, BlockPos blockPos, ItemStack itemStack, Vec3d vec) {
        spawnItem(world, Vec3d.of(blockPos), itemStack, vec);
    }
    public static void spawnItem(ServerWorld world, Vec3d blockPos, ItemStack itemStack, Vec3d vec) {
        world.spawnEntity(new ItemEntity(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack, vec.getX(), vec.getY(), vec.getZ()));
    }
}
