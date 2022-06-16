package com.rocketnotfound.rnf.forge.mixin.compat.hexerei;

import com.rocketnotfound.rnf.RNF;
import net.joefoxe.hexerei.block.custom.PickableDoubleFlower;
import net.joefoxe.hexerei.item.custom.FlowerOutputItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

import static net.joefoxe.hexerei.block.custom.PickableDoubleFlower.AGE;
import static net.joefoxe.hexerei.block.custom.PickableDoubleFlower.HALF;

@Pseudo
@Mixin(PickableDoubleFlower.class)
public class MixinHexereiPickableDoubleFlowerFix extends Block {
    @Shadow
    public int type;

    @Shadow
    public RegistryObject<FlowerOutputItem> firstOutput;

    @Shadow
    public RegistryObject<FlowerOutputItem> secondOutput;

    @Shadow
    public int maxSecondOutput;

    public MixinHexereiPickableDoubleFlowerFix(Settings arg) {
        super(arg);
    }

    @Inject(
        method = "Lnet/joefoxe/hexerei/block/custom/PickableDoubleFlower;randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void randomTickFix(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random, CallbackInfo ci) {
        if (!blockState.isAir() && blockState.getBlock() instanceof PickableDoubleFlower) {
            int i = (Integer) blockState.get(AGE);
            BlockState serverBlockState = serverWorld.getBlockState(blockPos);
            if (
                blockState.contains(HALF)
                && blockState.get(HALF) == DoubleBlockHalf.LOWER
                && i < 3
                && serverWorld.getBaseLightLevel(blockPos.up(), 0) >= 9
                && ForgeHooks.onCropsGrowPre(serverWorld, blockPos, blockState, random.nextInt(10) == 0)
                && blockState.contains(AGE)
                && !serverBlockState.isAir()
                && serverBlockState.getBlock() instanceof PickableDoubleFlower
            ) {
                serverWorld.setBlockState(blockPos, serverBlockState.with(AGE, i + 1), 2);

                BlockState upBlockState = serverWorld.getBlockState(blockPos.up());
                if (!upBlockState.isAir() && upBlockState.getBlock() == serverBlockState.getBlock()) {
                    serverWorld.setBlockState(blockPos.up(), upBlockState.with(AGE, i + 1), 2);
                } else if (upBlockState.isAir()) {
                    serverWorld.setBlockState(blockPos.up(), serverBlockState.with(AGE, i + 1).with(HALF, DoubleBlockHalf.UPPER), 3);
                }

                ForgeHooks.onCropsGrowPost(serverWorld, blockPos, blockState);
            }
        }

        ci.cancel();
    }

    @Inject(
        method = "Lnet/joefoxe/hexerei/block/custom/PickableDoubleFlower;onUse(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
        at = @At("HEAD"),
        cancellable = true
    )
    public void onUseFix(BlockState blockstate, World level, BlockPos blockpos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!blockstate.isAir() && blockstate.getBlock() instanceof PickableDoubleFlower) {
            int i = (Integer) blockstate.get(AGE);
            boolean flag = i == 3;
            if (!flag && player.getStackInHand(hand).isOf(Items.BONE_MEAL)) {
                cir.setReturnValue(ActionResult.PASS);
            } else if (i > 1) {
                ItemStack firstOutput = new ItemStack((ItemConvertible) this.firstOutput.get(), 4);
                ItemStack secondOutput = ItemStack.EMPTY;
                if (this.secondOutput != null) {
                    secondOutput = new ItemStack((ItemConvertible) this.secondOutput.get(), this.maxSecondOutput);
                }

                int j = Math.max(1, level.random.nextInt(firstOutput.getCount()));
                int k = 0;
                if (this.secondOutput != null) {
                    k = Math.max(1, level.random.nextInt(secondOutput.getCount()));
                }

                dropStack(level, blockpos, new ItemStack(firstOutput.getItem(), Math.max(1, (int) Math.floor((double) ((float) j / 2.0F))) + (flag ? (int) Math.ceil((double) ((float) j / 2.0F)) : 0)));
                if (level.random.nextInt(2) == 0 && this.secondOutput != null) {
                    dropStack(level, blockpos, new ItemStack(secondOutput.getItem(), Math.max(1, (int) Math.floor((double) ((float) k / 2.0F))) + (flag ? (int) Math.ceil((double) ((float) k / 2.0F)) : 0)));
                }

                level.playSound((PlayerEntity) null, blockpos, SoundEvents.BLOCK_CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);

                BlockState otherHalfBlockState;
                BlockState targetBlockState = level.getBlockState(blockpos);
                if (!targetBlockState.isAir() && targetBlockState.getBlock() instanceof PickableDoubleFlower) {
                    if (blockstate.get(HALF) == DoubleBlockHalf.LOWER) {
                        level.setBlockState(blockpos, targetBlockState.with(AGE, 0), 2);
                        otherHalfBlockState = level.getBlockState(blockpos.up());
                        if (!otherHalfBlockState.isAir() && otherHalfBlockState.getBlock() == targetBlockState.getBlock()) {
                            level.setBlockState(blockpos.up(), otherHalfBlockState.with(AGE, 0), 2);
                        } else if (otherHalfBlockState.isAir()) {
                            level.setBlockState(blockpos.up(), targetBlockState.with(AGE, 0).with(HALF, DoubleBlockHalf.UPPER), 3);
                        }
                    } else {
                        level.setBlockState(blockpos, targetBlockState.with(AGE, 0), 2);
                        otherHalfBlockState = level.getBlockState(blockpos.down());
                        if (!otherHalfBlockState.isAir() && otherHalfBlockState.getBlock() == targetBlockState.getBlock()) {
                            level.setBlockState(blockpos.down(), otherHalfBlockState.with(AGE, 0), 2);
                        } else if (otherHalfBlockState.isAir()) {
                            level.setBlockState(blockpos.down(), targetBlockState.with(AGE, 0).with(HALF, DoubleBlockHalf.LOWER), 3);
                        }
                    }

                    cir.setReturnValue(ActionResult.success(level.isClient));
                }
            } else {
                cir.setReturnValue(super.onUse(blockstate, level, blockpos, player, hand, hit));
            }
        }
        cir.setReturnValue(ActionResult.PASS);
    }

    @Inject(
        method = "Lnet/joefoxe/hexerei/block/custom/PickableDoubleFlower;isFertilizable(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    public void isFertilizableFix(BlockView p_57260_, BlockPos p_57261_, BlockState p_57262_, boolean p_57263_, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(p_57262_.contains(AGE) && (Integer)p_57262_.get(AGE) < 3);
    }

    @Inject(
        method = "Lnet/joefoxe/hexerei/block/custom/PickableDoubleFlower;grow(Lnet/minecraft/server/world/ServerWorld;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void growFix(ServerWorld level, Random random, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (
            !blockState.isAir()
            && blockState.getBlock() instanceof PickableDoubleFlower
            && blockState.contains(HALF)
            && blockState.contains(AGE)
        ) {
            int i;
            if (blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                if(
                    level.getBlockState(blockPos).getBlock() instanceof PickableDoubleFlower
                    && level.getBlockState(blockPos.up()).getBlock() instanceof PickableDoubleFlower
                ) {
                    i = Math.min(3, (Integer) blockState.get(AGE) + 1);
                    level.setBlockState(blockPos, (BlockState) blockState.with(AGE, i), 2);
                    level.setBlockState(blockPos.up(), (BlockState) level.getBlockState(blockPos.up()).with(AGE, i), 2);
                }
            } else {
                if(
                    level.getBlockState(blockPos).getBlock() instanceof PickableDoubleFlower
                    && level.getBlockState(blockPos.down()).getBlock() instanceof PickableDoubleFlower
                ) {
                    i = Math.min(3, (Integer) level.getBlockState(blockPos.down()).get(AGE) + 1);
                    level.setBlockState(blockPos, (BlockState) blockState.with(AGE, i), 2);
                    level.setBlockState(blockPos.down(), (BlockState) level.getBlockState(blockPos.down()).with(AGE, i), 2);
                }
            }
        }
        ci.cancel();
    }
}
