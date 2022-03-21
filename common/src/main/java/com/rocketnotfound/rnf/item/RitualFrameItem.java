package com.rocketnotfound.rnf.item;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.util.RitualFrameConnectionHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualFrameItem extends GeckoBlockItem {
    public static final int MAX_RANGE = 16;

    public RitualFrameItem(Settings settings) {
        super(RNFBlocks.RITUAL_FRAME.get(), settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (playerEntity.isSneaking() && itemStack.isOf(RNFItems.RITUAL_FRAME.get())) {
            if (world.isClient) {
                RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.self"), false);
            }
            itemStack.removeSubNbt("Target");
            return TypedActionResult.success(itemStack);
        }
        return super.use(world, playerEntity, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockPos blockPos = itemUsageContext.getBlockPos();
        World world = itemUsageContext.getWorld();
        PlayerEntity playerEntity = itemUsageContext.getPlayer();
        ItemStack itemStack = itemUsageContext.getStack();

        // Link the ritual frame item with target ritual frame
        if (playerEntity.isSneaking() && itemStack.isOf(RNFItems.RITUAL_FRAME.get())) {
            BlockEntity te = world.getBlockEntity(blockPos);
            if (te instanceof RitualFrameBlockEntity) {
                BlockPos target = RitualFrameConnectionHandler.checkTarget(world, blockPos);
                if (target != null) {
                    if (world.isClient) {
                        String langEntry = (target.equals(blockPos)) ? "ritual_frame.attune.set" : "ritual_frame.attune.redirect";
                        RNF.PROXY.sendOverlayMessage(new TranslatableText(langEntry, new Object[]{target.getX(), target.getY(), target.getZ()}), false);
                    }
                    itemStack.setSubNbt("Target", NbtHelper.fromBlockPos(target));
                    return ActionResult.SUCCESS;
                }
                return ActionResult.FAIL;
            }
        }

        // Warn players that they're placing the block too far, but allow them to place if they want to
        NbtCompound targetNbt = itemStack.getSubNbt("Target");
        if (targetNbt != null) {
            BlockPos targetPos = NbtHelper.toBlockPos(targetNbt);
            BlockPos checkedTarget = RitualFrameConnectionHandler.checkTarget(world, targetPos);
            if (!targetPos.equals(checkedTarget)) {
                if (world.isClient) {
                    RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.break"), false);
                }
                itemStack.removeSubNbt("Target");
                return ActionResult.FAIL;
            }

            if(!targetPos.isWithinDistance(blockPos, MAX_RANGE)) {
                NbtCompound warnNbt = itemStack.getSubNbt("Warn");
                if (warnNbt != null) {
                    BlockPos warnPos = NbtHelper.toBlockPos(warnNbt);
                    if (warnPos.equals(blockPos)) {
                        if (world.isClient) {
                            RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.self"), false);
                        }
                        itemStack.removeSubNbt("Target");
                        return super.useOnBlock(itemUsageContext);
                    }
                }

                if (world.isClient) {
                    RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.falter"), false);
                }
                itemStack.setSubNbt("Warn", NbtHelper.fromBlockPos(blockPos));
                return ActionResult.FAIL;
            }
        }

        return super.useOnBlock(itemUsageContext);
    }
}
