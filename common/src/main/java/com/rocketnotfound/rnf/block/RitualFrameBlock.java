package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.sound.RNFSounds;
import com.rocketnotfound.rnf.util.RitualFrameConnectionHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
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
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.UP));

    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if (be instanceof RitualFrameBlockEntity) {
            RitualFrameBlockEntity rfbe = (RitualFrameBlockEntity) be;
            NbtCompound target = itemStack.getSubNbt("Target");
            if (target != null) {
                BlockPos targetPos = NbtHelper.toBlockPos(target);
                BlockEntity targetBE = world.getBlockEntity(targetPos);
                if (targetBE instanceof RitualFrameBlockEntity) {
                    RitualFrameBlockEntity targetRFBE = (RitualFrameBlockEntity) targetBE;
                    RitualFrameConnectionHandler.target(rfbe, targetRFBE);
                }
            }
        }
        super.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
    }

    @Override
    public void onStateReplaced(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (blockState.hasBlockEntity() && (blockState.getBlock() != blockState2.getBlock() || !blockState2.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(blockPos);
            if (!(be instanceof RitualFrameBlockEntity))
                return;
            RitualFrameBlockEntity rfbe = (RitualFrameBlockEntity) be;
            RitualFrameConnectionHandler.remove(rfbe);

            ItemScatterer.spawn(world, blockPos, rfbe.getInventory());

            world.removeBlockEntity(blockPos);
        }
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ItemStack heldItem = playerEntity.getStackInHand(hand);

        BlockEntity te = world.getBlockEntity(blockPos);
        if (!(te instanceof RitualFrameBlockEntity)) {
            return ActionResult.PASS;
        }

        RitualFrameBlockEntity rfbe = (RitualFrameBlockEntity) te;
        ItemStack inSlot = rfbe.getItem();

        if (heldItem.isOf(RNFItems.RITUAL_STAFF.get())) {
            if (heldItem.getSubNbt("Debug") != null) {
                if (!world.isClient) {
                    playerEntity.sendMessage(Text.of(
                        String.format(
                            "--------------------------------\n" +
                                "Pos: %s\n" +
                                "Conductor: %s\n" +
                                "Target: %s\n" +
                                "Targetted By: %s\n" +
                                "Phase (Conductor): %s",
                            rfbe.getPos(), rfbe.getConductor(), rfbe.getTarget(), rfbe.getTargettedBy(), rfbe.getPhase()
                        )
                    ), false);
                }
                return ActionResult.SUCCESS;
            }
            if (heldItem.getSubNbt("Seeking") != null) {
                return ActionResult.PASS;
            }
        }

        if (heldItem.isEmpty() && inSlot.isEmpty()) {
            return ActionResult.PASS;
        }

        if (world.isClient) {
            RNF.PROXY.getClientPlayer().playSound(RNFSounds.RITUAL_GENERIC_CHANGE.get(), 1, 1);
        }

        ItemStack copy = heldItem.copy();
        copy.setCount(1);
        heldItem.decrement(1);

        if (!inSlot.isEmpty()) {
            playerEntity.getInventory().offerOrDrop(inSlot);
        }
        rfbe.setItem(copy);
        rfbe.updateBlock();

        return ActionResult.SUCCESS;
    }

    @Override
    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (entity instanceof ItemEntity) {
            BlockEntity be = world.getBlockEntity(blockPos);
            ItemStack itemStack = ((ItemEntity) entity).getStack();
            if (be instanceof RitualFrameBlockEntity) {
                RitualFrameBlockEntity rfbe = (RitualFrameBlockEntity) be;
                if (rfbe.getItem() == ItemStack.EMPTY && rfbe.isDormant()) {
                    ItemStack copy = itemStack.copy();
                    copy.setCount(1);
                    rfbe.setItem(copy);
                    itemStack.decrement(1);
                    rfbe.updateBlock();
                }
            }
        }
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
        return PistonBehavior.DESTROY;
    }
}
