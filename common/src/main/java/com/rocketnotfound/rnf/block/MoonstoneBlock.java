package com.rocketnotfound.rnf.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class MoonstoneBlock extends Block {
    public static final BooleanProperty LIT;

    static {
        LIT = Properties.LIT;
    }

    public MoonstoneBlock(Settings builder) {
        super(builder);
        this.setDefaultState((BlockState)this.getDefaultState().with(LIT, false));
    }

    public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
        light(blockState, world, blockPos);
        super.onBlockBreakStart(blockState, world, blockPos, playerEntity);
    }

    public void onSteppedOn(World world, BlockPos blockPos, BlockState blockState, Entity entity) {
        light(blockState, world, blockPos);
        super.onSteppedOn(world, blockPos, blockState, entity);
    }

    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            spawnParticles(world, blockPos);
        } else {
            light(blockState, world, blockPos);
        }

        ItemStack itemStack = playerEntity.getStackInHand(hand);
        return itemStack.getItem() instanceof BlockItem && (new ItemPlacementContext(playerEntity, hand, itemStack, blockHitResult)).canPlace() ? ActionResult.PASS : ActionResult.SUCCESS;
    }

    private static void light(BlockState blockState, World world, BlockPos blockPos) {
        spawnParticles(world, blockPos);
        if (!(Boolean)blockState.get(LIT)) {
            world.setBlockState(blockPos, (BlockState)blockState.with(LIT, true), 3);
        }

    }

    public boolean hasRandomTicks(BlockState blockState) {
        return (Boolean)blockState.get(LIT);
    }

    public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if ((Boolean)blockState.get(LIT)) {
            serverWorld.setBlockState(blockPos, (BlockState)blockState.with(LIT, false), 3);
        }

    }

    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if ((Boolean)blockState.get(LIT)) {
            spawnParticles(world, blockPos);
        }

    }

    private static void spawnParticles(World world, BlockPos blockPos) {
        double d = 0.5625D;
        Random random = world.random;
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Direction direction = var5[var7];
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!world.getBlockState(blockPos2).isOpaqueFullCube(world, blockPos2)) {
                Direction.Axis axis = direction.getAxis();
                double e = axis == Direction.Axis.X ? 0.5D + d * (double)direction.getOffsetX() : (double)random.nextFloat();
                double f = axis == Direction.Axis.Y ? 0.5D + d * (double)direction.getOffsetY() : (double)random.nextFloat();
                double g = axis == Direction.Axis.Z ? 0.5D + d * (double)direction.getOffsetZ() : (double)random.nextFloat();
                if (random.nextInt(5) == 0) {
                    world.addParticle(ParticleTypes.END_ROD, (double) blockPos.getX() + e, (double) blockPos.getY() + f, (double) blockPos.getZ() + g, 0.0D, 0.0D, 0.0D);
                }
            }
        }

    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT});
    }
}
