package com.rocketnotfound.rnf.item;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class GeckoItem extends Item implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public GeckoItem(Settings settings) {
        super(settings);
    }

    @SuppressWarnings("unchecked")
    protected <E extends GeckoItem> PlayState predicate(AnimationEvent<E> event) {
        return PlayState.STOP;
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
