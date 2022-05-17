package com.rocketnotfound.rnf.util;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ItemEntityHelper {
    public static final ItemStack SILK_TOUCH_STICK;
    static {
         ItemStack silkStick = new ItemStack(Items.STICK);
         silkStick.addEnchantment(Enchantments.SILK_TOUCH, 1);
         SILK_TOUCH_STICK = silkStick;
    }

    public static final BiFunction<Integer, Boolean, ItemStack> FORTUNE_SILK_TOOL_HELPER = (fortuneLevel, hasSilk) -> {
        ItemStack silk = (hasSilk) ? SILK_TOUCH_STICK.copy() : new ItemStack(Items.STICK);
        silk.addEnchantment(Enchantments.FORTUNE, fortuneLevel);
        return silk;
    };

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
