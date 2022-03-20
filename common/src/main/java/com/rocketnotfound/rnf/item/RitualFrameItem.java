package com.rocketnotfound.rnf.item;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualFrameItem extends GeckoBlockItem {
    public RitualFrameItem(Settings settings) {
        super(RNFBlocks.RITUAL_FRAME.get(), settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockPos blockPos = itemUsageContext.getBlockPos();
        World world = itemUsageContext.getWorld();
        PlayerEntity playerEntity = itemUsageContext.getPlayer();
        ItemStack itemStack = itemUsageContext.getStack();
        BlockEntity te = world.getBlockEntity(blockPos);
        if (te instanceof RitualFrameBlockEntity && playerEntity.isSneaking() && itemStack.isOf(RNFItems.RITUAL_FRAME.get())) {
            if (world.isClient) {
                RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_frame.attune_to", new Object[] {blockPos.getX(), blockPos.getY(), blockPos.getZ()}), false);
            }
            itemStack.setSubNbt("Target", NbtHelper.fromBlockPos(blockPos));
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(itemUsageContext);
    }
}
