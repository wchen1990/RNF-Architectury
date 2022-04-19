package com.rocketnotfound.rnf.client.model;

import com.rocketnotfound.rnf.item.RitualPrimerItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RitualPrimerItemModel extends AnimatedGeoModel<RitualPrimerItem> {
    @Override
    public Identifier getModelLocation(RitualPrimerItem item) {
        return createIdentifier("geo/ritual_primer.geo.json");
    }

    @Override
    public Identifier getAnimationFileLocation(RitualPrimerItem animatable) {
        return createIdentifier("animations/ritual_primer.animation.json");
    }

    @Override
    public Identifier getTextureLocation(RitualPrimerItem item) {
        return createIdentifier("textures/block/ritual_primer.png");
    }
}
