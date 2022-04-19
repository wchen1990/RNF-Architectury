package com.rocketnotfound.rnf.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

import static com.rocketnotfound.rnf.block.RNFBlocks.blockLuminance;

public class RuneBlock extends TransparentBlock implements Stainable {
    public static final DirectionProperty FACING;
    static {
        FACING = Properties.HORIZONTAL_FACING;
    }

    public RuneBlock() {
        this(
            AbstractBlock.Settings.of(Material.GLASS, MapColor.TERRACOTTA_WHITE)
                .nonOpaque()
                .luminance(blockLuminance(12))
                .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                .strength(1.5f, 6.0f)
        );
    }

    public RuneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }
}
