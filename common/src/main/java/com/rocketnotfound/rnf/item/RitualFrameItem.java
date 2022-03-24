package com.rocketnotfound.rnf.item;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.util.RitualFrameConnectionHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.rocketnotfound.rnf.item.RNFItems.customName;

public class RitualFrameItem extends GeckoBlockItem {
    public RitualFrameItem(Settings settings) {
        super(RNFBlocks.RITUAL_FRAME.get(), settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (playerEntity.isSneaking() && itemStack.isOf(RNFItems.RITUAL_FRAME.get())) {
            if (world.isClient) {
                RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,1,1);
                RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.self", new Object[]{ customName(itemStack) }), false);
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
                        RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,1,1);
                        RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.set", new Object[]{ customName(itemStack) }), false);
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
            if(!targetPos.isWithinDistance(blockPos, RNF.serverConfig().MAX_RANGE)) {
                NbtCompound warnNbt = itemStack.getSubNbt("Warn");
                if (warnNbt != null) {
                    BlockPos warnPos = NbtHelper.toBlockPos(warnNbt);
                    if (warnPos.equals(blockPos)) {
                        if (world.isClient) {
                            RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,1,1);
                            RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.break", new Object[]{ customName(itemStack) }), false);
                        }
                        itemStack.removeSubNbt("Target");
                        return super.useOnBlock(itemUsageContext);
                    }
                }

                if (world.isClient) {
                    RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_FALL,1,1);
                    RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune.falter", new Object[]{ customName(itemStack) }), false);
                }
                itemStack.setSubNbt("Warn", NbtHelper.fromBlockPos(blockPos));
                return ActionResult.FAIL;
            }
        }

        return super.useOnBlock(itemUsageContext);
    }
}
