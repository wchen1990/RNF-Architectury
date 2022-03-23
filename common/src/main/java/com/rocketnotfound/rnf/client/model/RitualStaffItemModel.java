package com.rocketnotfound.rnf.client.model;

import com.rocketnotfound.rnf.item.RitualStaffItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RitualStaffItemModel extends AnimatedGeoModel<RitualStaffItem> {
    @Override
    public Identifier getModelLocation(RitualStaffItem item) {
        return createIdentifier("geo/ritual_staff.geo.json");
    }

    @Override
    public Identifier getAnimationFileLocation(RitualStaffItem animatable) {
        return createIdentifier("animations/ritual_staff.animation.json");
    }

    @Override
    public Identifier getTextureLocation(RitualStaffItem item) {
        return createIdentifier("textures/item/ritual_staff.png");
    }
}
