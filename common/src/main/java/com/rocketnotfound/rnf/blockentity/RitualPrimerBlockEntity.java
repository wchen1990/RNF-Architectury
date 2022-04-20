package com.rocketnotfound.rnf.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class RitualPrimerBlockEntity extends BaseBlockEntity implements IAnimatable {
    protected String targetDimension;
    protected BlockPos targetPosition;

    private final AnimationFactory factory = new AnimationFactory(this);

    public RitualPrimerBlockEntity(BlockPos pos, BlockState state) {
        super(RNFBlockEntities.RITUAL_PRIMER.get(), pos, state);
    }

    // NBT
    @Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);

        if (nbtCompound.contains("TargetDimension"))
            targetDimension = nbtCompound.getString("TargetDimension");
        if (nbtCompound.contains("TargetPosition"))
            targetPosition = NbtHelper.toBlockPos(nbtCompound.getCompound("TargetPosition"));
    }

    @Override
    public void writeNbt(NbtCompound nbtCompound) {
        super.writeNbt(nbtCompound);

        if (targetDimension != null)
            nbtCompound.putString("TargetDimension", targetDimension);
        if (targetPosition != null)
            nbtCompound.put("TargetPosition", NbtHelper.fromBlockPos(targetPosition));
    }

    // Geckolib code
    @SuppressWarnings("unchecked")
    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().transitionLengthTicks = 0;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ritual_primer.idle", true));
        return PlayState.CONTINUE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
