package com.rocketnotfound.rnf.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RitualStandBlockEntity extends BaseBlockEntity {
    public RitualStandBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RNFBlockEntities.RITUAL_STAND.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, RitualStandBlockEntity blockEntity) {
    }
}
