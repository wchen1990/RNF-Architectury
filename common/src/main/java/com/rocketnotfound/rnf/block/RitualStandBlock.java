package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.blockentity.RitualStandBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;

import javax.annotation.Nullable;

public class RitualStandBlock extends BlockWithEntity {
    public RitualStandBlock(Settings builder) {
        super(builder);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return RNFBlockEntities.RITUAL_STAND.get().instantiate(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return checkType($$2, RNFBlockEntities.RITUAL_STAND.get(), RitualStandBlockEntity::tick);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getGameEventListener(World $$0, T $$1) {
        return super.getGameEventListener($$0, $$1);
    }
}
