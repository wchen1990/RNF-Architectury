package com.rocketnotfound.rnf.item;

import com.rocketnotfound.rnf.RNF;
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
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

import static com.rocketnotfound.rnf.item.RNFItems.customName;

public class RitualStaffItem extends GeckoItem {
    public RitualStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (playerEntity.isSneaking() && itemStack.isOf(RNFItems.RITUAL_STAFF.get())) {
            if (itemStack.getSubNbt("Target") != null) {
                if (world.isClient) {
                    RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,1,1);
                    RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_staff.attune.self", new Object[]{ customName(itemStack) }), false);
                }
                itemStack.removeSubNbt("Target");
            } else if (itemStack.getSubNbt("Seeking") != null) {
                if (world.isClient) {
                    RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,1,1);
                    RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_staff.attune.dormant", new Object[]{ customName(itemStack) }), false);
                }
                itemStack.removeSubNbt("Seeking");
            } else {
                if (world.isClient) {
                    RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP,1,1);
                    RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_staff.attune.self", new Object[]{ customName(itemStack) }), false);
                }
                itemStack.setSubNbt("Seeking", new NbtCompound());
            }
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
        BlockEntity te = world.getBlockEntity(blockPos);

        if (itemStack.isOf(RNFItems.RITUAL_STAFF.get())) {
            if (te instanceof RitualFrameBlockEntity) {
                if (itemStack.getSubNbt("Seeking") != null) {
                    if (playerEntity.isSneaking()) {
                        if (world.isClient) {
                            RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, 1, 1);
                            RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_staff.attune.conduct", new Object[]{customName(itemStack)}), false);
                        }
                        RitualFrameConnectionHandler.makeConductor((RitualFrameBlockEntity) te);
                        return ActionResult.SUCCESS;
                    } else {
                        NbtCompound targetNbt = itemStack.getSubNbt("Target");
                        if (targetNbt != null) {
                            BlockPos target = NbtHelper.toBlockPos(targetNbt);
                            if (!target.equals(blockPos)) {
                                if (!target.isWithinDistance(blockPos, RNF.serverConfig().MAX_RANGE)) {
                                    if (world.isClient) {
                                        RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1);
                                        RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_staff.attune.break", new Object[]{customName(itemStack)}), false);
                                    }
                                    itemStack.removeSubNbt("Target");
                                    return ActionResult.FAIL;
                                }

                                BlockEntity targetBE = world.getBlockEntity(target);
                                if (targetBE instanceof RitualFrameBlockEntity) {
                                    if (world.isClient) {
                                        RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_PLACE, 1, 1);
                                        RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_staff.attune.link"), false);
                                    }
                                    RitualFrameConnectionHandler.target((RitualFrameBlockEntity) targetBE, (RitualFrameBlockEntity) te);
                                    itemStack.removeSubNbt("Target");
                                    return ActionResult.SUCCESS;
                                }
                            }
                            return ActionResult.FAIL;
                        } else {
                            if (world.isClient) {
                                RNF.PROXY.getClientPlayer().playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, 1, 1);
                                RNF.PROXY.sendOverlayMessage(new TranslatableText("ritual_staff.attune.set", new Object[]{customName(itemStack)}), false);
                            }
                            itemStack.setSubNbt("Target", NbtHelper.fromBlockPos(blockPos));
                            return ActionResult.SUCCESS;
                        }
                    }
                }
            }
        }

        return super.useOnBlock(itemUsageContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <E extends GeckoItem> PlayState predicate(AnimationEvent<E> event) {
        event.getController().transitionLengthTicks = 0;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ritual_staff.pillar_idle", true));
        return PlayState.CONTINUE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }
}
