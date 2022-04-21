package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.blockentity.RitualTranscriberBlockEntity;
import com.rocketnotfound.rnf.item.RNFItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.Random;

import static com.rocketnotfound.rnf.block.RNFBlocks.blockLuminanceWithProperty;

public class RitualTranscriberBlock extends BlockWithEntity {
    public static final DirectionProperty FACING;
    public static final BooleanProperty POWERED;
    static {
        FACING = Properties.FACING;
        POWERED = Properties.POWERED;
    }

    public RitualTranscriberBlock() {
        this(
            AbstractBlock.Settings.of(Material.STONE, MapColor.BLACK)
                .requiresTool()
                .luminance(blockLuminanceWithProperty(15, POWERED,6))
                .strength(25.0F, 600.0f)
        );
    }

    public RitualTranscriberBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        ItemStack heldItem = playerEntity.getStackInHand(hand);

        BlockEntity te = world.getBlockEntity(blockPos);
        if (!(te instanceof RitualTranscriberBlockEntity)) {
            return ActionResult.PASS;
        }

        RitualTranscriberBlockEntity rtbe = (RitualTranscriberBlockEntity) te;

        if (heldItem.isOf(RNFItems.RITUAL_STAFF.get())) {
            if (heldItem.getSubNbt("Debug") != null) {
                if (!world.isClient) {
                    playerEntity.sendMessage(Text.of(
                        String.format(
                            "--------------------------------\n" +
                                "Pos: %s\n" +
                                "Phase: %s",
                            rtbe.getPos(),
                            rtbe.getPhase()
                        )
                    ), false);
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return RNFBlockEntities.RITUAL_TRANSCRIBER.get().instantiate(blockPos, blockState);
        //return new RitualTranscriberBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return checkType(blockEntityType, RNFBlockEntities.RITUAL_TRANSCRIBER.get(), RitualTranscriberBlockEntity::tick);
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
            .with(FACING, itemPlacementContext.getSide().getOpposite())
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
