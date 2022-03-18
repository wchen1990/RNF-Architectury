package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import javax.annotation.Nullable;

public class RitualFrameBlock extends Block implements BlockEntityProvider, Waterloggable {
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

    public RitualFrameBlock(Settings builder) {
        super(builder);
        this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, false)).with(FACING, Direction.UP));

    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ItemStack heldItem = playerEntity.getStackInHand(hand);
        boolean isHand = heldItem.isEmpty() && hand == Hand.MAIN_HAND;

        BlockEntity te = world.getBlockEntity(blockPos);
        if (!(te instanceof RitualFrameBlockEntity))
            return ActionResult.PASS;
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return RNFBlockEntities.RITUAL_FRAME.get().instantiate(blockPos, blockState);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return checkType($$2, RNFBlockEntities.RITUAL_FRAME.get(), RitualFrameBlockEntity::tick);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        Direction direction = (Direction)blockState.get(FACING);
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
        Direction direction = (Direction)blockState.get(FACING);
        BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
        return worldView.getBlockState(blockPos2).isSideSolidFullSquare(worldView, blockPos2, direction);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState2, WorldAccess worldAccess, BlockPos blockPos, BlockPos blockPos2) {
        if ((Boolean)blockState.get(WATERLOGGED)) {
            worldAccess.createAndScheduleFluidTick(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(worldAccess));
        }

        return direction == ((Direction)blockState.get(FACING)).getOpposite() && !blockState.canPlaceAt(worldAccess, blockPos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(blockState, direction, blockState2, worldAccess, blockPos, blockPos2);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        WorldAccess worldAccess = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        return (BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER)).with(FACING, itemPlacementContext.getSide());
    }

    @Override
    public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
        return (BlockState)blockState.with(FACING, blockRotation.rotate((Direction)blockState.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
        return blockState.rotate(blockMirror.getRotation((Direction)blockState.get(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return (Boolean)blockState.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(blockState);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{WATERLOGGED, FACING});
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState blockState) {
        return PistonBehavior.DESTROY;
    }
}
