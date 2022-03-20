package com.rocketnotfound.rnf.blockentity;

import net.minecraft.util.math.BlockPos;

public interface IMultiBlockEntityContainer {
    BlockPos getConductor();
    boolean isConductor();
    void setConductor(BlockPos pos);
    BlockPos getLastKnownPos();
}
