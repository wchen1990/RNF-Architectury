package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.particle.LunaParticleEffect;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class LunaBlock extends HorizontalFacingBlock implements Wearable {
    public static final BooleanProperty LIT;

    static {
        LIT = Properties.LIT;
    }

    public LunaBlock() {
        super(
            AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE)
                .ticksRandomly()
                .luminance((blockState) -> blockState.get(Properties.LIT) ? 10 : 7)
                .sounds(BlockSoundGroup.STONE)
                .strength(1.5f, 6.0f)
        );
        this.setDefaultState(this.getDefaultState().with(LIT, false).with(FACING, Direction.NORTH));
    }

    @Override
    public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
        light(blockState, world, blockPos);
        super.onBlockBreakStart(blockState, world, blockPos, playerEntity);
    }

    @Override
    public void onSteppedOn(World world, BlockPos blockPos, BlockState blockState, Entity entity) {
        light(blockState, world, blockPos);
        super.onSteppedOn(world, blockPos, blockState, entity);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (world.isClient) {
            spawnParticles(world, blockPos);
        } else {
            light(blockState, world, blockPos);
        }

        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (itemStack.isOf(RNFItems.RITUAL_STAFF.get())) {
            if (itemStack.getSubNbt("Debug") != null) {
                if (!world.isClient) {
                    playerEntity.sendMessage(Text.of(
                        String.format(
                            "--------------------------------\n" +
                            "Pos: %s\n" +
                            "Time: %s\n" +
                            "TimeOfDay: %s\n" +
                            "LunarTime: %s\n" +
                            "MoonPhase: %s\n" +
                            "IsDay: %s\n" +
                            "IsNight: %s",
                            blockPos,
                            world.getTime(),
                            world.getTimeOfDay(),
                            world.getLunarTime(),
                            world.getMoonPhase(),
                            world.isDay(),
                            // The client does not show the correct value so this can't be used for anything client side
                            world.isNight()
                        )
                    ), false);
                }
                return ActionResult.SUCCESS;
            }
        }

        return itemStack.getItem() instanceof BlockItem && (new ItemPlacementContext(playerEntity, hand, itemStack, blockHitResult)).canPlace() ? ActionResult.PASS : ActionResult.SUCCESS;
    }

    private static void light(BlockState blockState, World world, BlockPos blockPos) {
        spawnParticles(world, blockPos);
        if (!(Boolean)blockState.get(LIT)) {
            world.setBlockState(blockPos, blockState.with(LIT, true), 3);
        }

    }

    @Override
    public boolean hasRandomTicks(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
        if (blockState.get(LIT)) {
            if (!serverWorld.isNight() || !serverWorld.isSkyVisibleAllowingSea(blockPos)) {
                serverWorld.setBlockState(blockPos, blockState.with(LIT, false), 3);
            }
        } else {
            if (serverWorld.isNight() && serverWorld.isSkyVisibleAllowingSea(blockPos)) {
                serverWorld.setBlockState(blockPos, blockState.with(LIT, true), 3);
            }
        }

    }

    @Override
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        if (blockState.get(LIT)) {
            spawnParticles(world, blockPos);
        }
    }

    private static void spawnParticles(World world, BlockPos blockPos) {
        double d = 0.5625D;
        Random random = world.random;
        Direction[] var5 = Direction.values();
        int var6 = var5.length;

        if(world.isSkyVisibleAllowingSea(blockPos)) {
            BlockState bs = world.getBlockState(blockPos);
            if (bs.isOf(RNFBlocks.LUNA_BLOCK.get())) {
                Direction facing = bs.get(FACING);

                double xr = 0, yr = 0, zr = 0;
                double
                    xrd = 2 + random.nextFloat(),
                    yrd = MathHelper.lerp(random.nextFloat(), -0.4, 0.4),
                    zrd = 2 + random.nextFloat();

                float rot = 10 + random.nextFloat(), ramp = 20;
                boolean swap = facing.equals(Direction.NORTH) || facing.equals(Direction.SOUTH);
                int maxAge = 100;

                Vec3d modVec = new Vec3d(
                    facing.getOffsetX() != 0 ? facing.getOffsetX() : 1,
                    facing.getOffsetY() != 0 ? facing.getOffsetY() : 1,
                    facing.getOffsetZ() != 0 ? facing.getOffsetZ() : 1
                );

                double
                    originX = blockPos.getX() + 0.5,
                    originY = blockPos.getY() + 0.5,
                    originZ = blockPos.getZ() + 0.5;

                Vec3d radiiVec = new Vec3d(xr, yr, zr).multiply(modVec);
                Vec3d radiiDeltaVec = new Vec3d(xrd, yrd, zrd).multiply(modVec);

                if (random.nextInt(3) == 0) {
                    world.addParticle(
                        new LunaParticleEffect(
                            new Vec3f(radiiVec),
                            new Vec3f(radiiDeltaVec),
                            rot,
                            ramp,
                            swap,
                            maxAge
                        ),
                        originX, originY, originZ,
                        0.0D, 0.0D, 0.0D
                    );
                    world.addParticle(
                        new LunaParticleEffect(
                            new Vec3f(radiiVec.multiply(-1)),
                            new Vec3f(radiiDeltaVec.multiply(-1)),
                            rot,
                            ramp,
                            swap,
                            maxAge
                        ),
                        originX, originY, originZ,
                        0.0D, 0.0D, 0.0D
                    );
                }
            }
        } else {
            for (int var7 = 0; var7 < var6; ++var7) {
                Direction direction = var5[var7];
                BlockPos blockPos2 = blockPos.offset(direction);
                if (!world.getBlockState(blockPos2).isOpaqueFullCube(world, blockPos2)) {
                    Direction.Axis axis = direction.getAxis();
                    double e = axis == Direction.Axis.X ? 0.5D + d * (double) direction.getOffsetX() : (double) random.nextFloat();
                    double f = axis == Direction.Axis.Y ? 0.5D + d * (double) direction.getOffsetY() : (double) random.nextFloat();
                    double g = axis == Direction.Axis.Z ? 0.5D + d * (double) direction.getOffsetZ() : (double) random.nextFloat();

                    if (random.nextInt(5) == 0) {
                        world.addParticle(ParticleTypes.END_ROD, (double) blockPos.getX() + e, (double) blockPos.getY() + f, (double) blockPos.getZ() + g, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
        return this.getDefaultState().with(FACING, itemPlacementContext.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ LIT, FACING });
    }
}
