package com.rocketnotfound.rnf.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.Random;

import static com.rocketnotfound.rnf.block.RNFBlocks.blockLuminanceWithProperty;

public class RitualTranscriber extends FacingBlock {
    public static final BooleanProperty POWERED;
    static {
        POWERED = Properties.POWERED;
    }

    public RitualTranscriber() {
        this(
            AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK)
                .requiresTool()
                .luminance(blockLuminanceWithProperty(15, POWERED,6))
                .strength(25.0F, 600.0f)
        );
    }

    public RitualTranscriber(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        if (!world.isClient) {
            boolean bl2 = blockState.get(POWERED);
            if (bl2 != world.isReceivingRedstonePower(blockPos)) {
                if (bl2) {
                    world.createAndScheduleBlockTick(blockPos, this, 4);
                } else {
                    world.setBlockState(blockPos, blockState.cycle(POWERED), 2);
                }
            }

        }
    }

    @Override
    public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (blockState.get(POWERED) && !serverWorld.isReceivingRedstonePower(blockPos)) {
            serverWorld.setBlockState(blockPos, blockState.cycle(POWERED), 2);
        }

    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState()
            .with(FACING, itemPlacementContext.getSide())
            .with(POWERED, itemPlacementContext.getWorld().isReceivingRedstonePower(itemPlacementContext.getBlockPos()));
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
        builder.add(new Property[]{FACING, POWERED});
    }
}
