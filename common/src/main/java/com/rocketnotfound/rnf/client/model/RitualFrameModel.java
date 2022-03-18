package com.rocketnotfound.rnf.client.model;

import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
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

    // Common rotation code between both modloaders
    public static void rotateBlock(Direction facing, MatrixStack stack) {
        switch (facing) {
            case SOUTH:
                stack.translate(0,0.5,-0.5);
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                break;
            case WEST:
                stack.translate(0.5,0.5,0);
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
                break;
            case NORTH:
                stack.translate(0,0.5,0.5);
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(270));
                break;
            case EAST:
                stack.translate(-0.5,0.5,0);
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(270));
                break;
            case UP:
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0));
                break;
            case DOWN:
                stack.translate(0,1,0);
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                break;
        }
    }
}
