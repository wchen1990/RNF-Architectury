package com.rocketnotfound.rnf.forge.mixin.compat.hexerei;

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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Final;
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
    public void randomTickFix(BlockState p_57286_, ServerWorld p_57287_, BlockPos p_57288_, Random p_57289_, CallbackInfo ci) {
        if (p_57286_.contains(AGE)) {
            int i = (Integer) p_57286_.get(AGE);
            if (
                p_57286_.contains(HALF)
                && p_57286_.get(HALF) == DoubleBlockHalf.LOWER
                && i < 3
                && p_57287_.getBaseLightLevel(p_57288_.up(), 0) >= 9
                && ForgeHooks.onCropsGrowPre(p_57287_, p_57288_, p_57286_, p_57289_.nextInt(10) == 0)
                && p_57286_.contains(AGE)
                && p_57287_.getBlockState(p_57288_.up()).contains(AGE)
            ) {
                p_57287_.setBlockState(p_57288_, (BlockState) p_57286_.with(AGE, i + 1), 2);
                p_57287_.setBlockState(p_57288_.up(), (BlockState) p_57287_.getBlockState(p_57288_.up()).with(AGE, i + 1), 2);
                ForgeHooks.onCropsGrowPost(p_57287_, p_57288_, p_57286_);
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
        if (blockstate.contains(AGE)) {
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
                BlockState blockState;
                if (blockstate.get(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlockState(blockpos, (BlockState) blockstate.with(AGE, 0), 2);
                    blockState = level.getBlockState(blockpos.up());
                    if (blockState.getBlock() == level.getBlockState(blockpos).getBlock()) {
                        level.setBlockState(blockpos.up(), (BlockState) blockState.with(AGE, 0), 2);
                    } else if (blockState.isAir()) {
                        level.setBlockState(blockpos.up(), (BlockState) ((BlockState) level.getBlockState(blockpos).with(AGE, 0)).with(HALF, DoubleBlockHalf.UPPER), 2);
                    }
                } else {
                    level.setBlockState(blockpos, (BlockState) blockstate.with(AGE, 0), 2);
                    blockState = level.getBlockState(blockpos.down());
                    if (blockState.getBlock() == level.getBlockState(blockpos).getBlock()) {
                        level.setBlockState(blockpos.down(), (BlockState) blockState.with(AGE, 0), 2);
                    } else if (blockState.isAir()) {
                        level.setBlockState(blockpos.down(), (BlockState) ((BlockState) level.getBlockState(blockpos).with(AGE, 0)).with(HALF, DoubleBlockHalf.LOWER), 2);
                    }
                }

                cir.setReturnValue(ActionResult.success(level.isClient));
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
        if (blockState.contains(HALF) && blockState.contains(AGE)) {
            int i;
            if (blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                i = Math.min(3, (Integer) blockState.get(AGE) + 1);
                level.setBlockState(blockPos, (BlockState) blockState.with(AGE, i), 2);
                level.setBlockState(blockPos.up(), (BlockState) level.getBlockState(blockPos.up()).with(AGE, i), 2);
            } else {
                i = Math.min(3, (Integer) level.getBlockState(blockPos.down()).get(AGE) + 1);
                level.setBlockState(blockPos, (BlockState) blockState.with(AGE, i), 2);
                level.setBlockState(blockPos.down(), (BlockState) level.getBlockState(blockPos.down()).with(AGE, i), 2);
            }
        }
        ci.cancel();
    }
}
