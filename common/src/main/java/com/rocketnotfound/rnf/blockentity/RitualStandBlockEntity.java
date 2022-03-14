package com.rocketnotfound.rnf.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualStandBlockEntity extends BaseBlockEntity {
    public RitualStandBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RNFBlockEntities.RITUAL_STAND.get(), blockPos, blockState);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, RitualStandBlockEntity blockEntity) {
    }
}
