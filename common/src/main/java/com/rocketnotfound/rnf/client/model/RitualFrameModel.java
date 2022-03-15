package com.rocketnotfound.rnf.client.model;

import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RitualFrameModel extends AnimatedGeoModel<RitualFrameBlockEntity> {
    @Override
    public Identifier getModelLocation(RitualFrameBlockEntity entity) {
        return createIdentifier("geo/ritual_frame.geo.json");
    }

    @Override
    public Identifier getAnimationFileLocation(RitualFrameBlockEntity animatable) {
        return createIdentifier("animations/ritual_frame.animation.json");
    }

    @Override
    public Identifier getTextureLocation(RitualFrameBlockEntity entity) {
        return createIdentifier("textures/block/ritual_frame.png");
    }
}
