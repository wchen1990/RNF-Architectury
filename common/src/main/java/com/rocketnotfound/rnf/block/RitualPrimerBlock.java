package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import javax.annotation.Nullable;

import static com.rocketnotfound.rnf.block.RNFBlocks.blockLuminance;

public class RitualPrimerBlock extends Block implements BlockEntityProvider, Waterloggable {
    public static final BooleanProperty WATERLOGGED;
    public static final DirectionProperty FACING;

    static {
        WATERLOGGED = Properties.WATERLOGGED;
        FACING = Properties.FACING;
    }

    protected static final VoxelShape UP_SHAPE = Block.createCuboidShape(4.00, 0.0D, 4.00, 12.00, 4.00, 12.00);
    protected static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(4.00, 12.00, 4.00, 12.00, 16.0D, 12.00);
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(4.00, 4.00, 12.00, 12.00, 12.00, 16.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(4.00, 4.00, 0.0D, 12.00, 12.00, 4.00);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0D, 4.00, 4.00, 4.00, 12.00, 12.00);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(12.00, 4.00, 4.00, 16.0D, 12.00, 12.00);

    public RitualPrimerBlock() {
        this(
            AbstractBlock.Settings.of(Material.DECORATION, MapColor.TERRACOTTA_WHITE)
                .nonOpaque()
                .noCollision()
                .requiresTool()
                .luminance(blockLuminance(6))
                .sounds(BlockSoundGroup.AMETHYST_CLUSTER)
                .strength(1.5f, 6.0f)
        );
    }

    public RitualPrimerBlock(Settings builder) {
        super(builder);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.UP));
    }

    @Override
    public boolean emitsRedstonePower(BlockState blockState) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
        return 15;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return RNFBlockEntities.RITUAL_PRIMER.get().instantiate(blockPos, blockState);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World $$0, BlockState $$1, BlockEntityType<T> $$2) {
        //return checkType($$2, RNFBlockEntities.RITUAL_PRIMER.get(), RitualPrimerBlockEntity::tick);
        return null;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        Direction direction = blockState.get(FACING);
        switch(direction) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case DOWN:
                return DOWN_SHAPE;
            case UP:
            default:
                return UP_SHAPE;
        }
    }

    @Override
    public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
        Direction direction = blockState.get(FACING);
        BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
        return worldView.getBlockState(blockPos2).isSideSolidFullSquare(worldView, blockPos2, direction);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess worldAccess, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.get(WATERLOGGED)) {
            worldAccess.createAndScheduleFluidTick(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(worldAccess));
        }

        return direction == blockState.get(FACING).getOpposite() && !blockState.canPlaceAt(worldAccess, blockPos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(blockState, direction, blockState2, worldAccess, blockPos, blockPos2);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        WorldAccess worldAccess = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        return this.getDefaultState().with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER).with(FACING, itemPlacementContext.getSide());
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
    public FluidState getFluidState(BlockState blockState) {
        return blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING);
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState) {
        return PistonBehavior.BLOCK;
    }
}
