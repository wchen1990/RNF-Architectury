package com.rocketnotfound.rnf.client.model;

import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.item.RitualFrameItem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RitualFrameItemModel extends AnimatedGeoModel<RitualFrameItem> {
    @Override
    public Identifier getModelLocation(RitualFrameItem item) {
        return createIdentifier("geo/ritual_frame.geo.json");
    }

    @Override
    public Identifier getAnimationFileLocation(RitualFrameItem animatable) {
        return createIdentifier("animations/ritual_frame.animation.json");
    }

    @Override
    public Identifier getTextureLocation(RitualFrameItem item) {
        return createIdentifier("textures/block/ritual_frame.png");
    }
}
