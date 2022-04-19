package com.rocketnotfound.rnf.client.model;

import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.blockentity.RitualPrimerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.profiler.Profiler;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RitualPrimerModel extends AnimatedGeoModel<RitualPrimerBlockEntity> {
    @Override
    public Identifier getModelLocation(RitualPrimerBlockEntity entity) {
        return createIdentifier("geo/ritual_primer.geo.json");
    }

    @Override
    public Identifier getAnimationFileLocation(RitualPrimerBlockEntity animatable) {
        return createIdentifier("animations/ritual_primer.animation.json");
    }

    @Override
    public Identifier getTextureLocation(RitualPrimerBlockEntity entity) {
        return createIdentifier("textures/block/ritual_primer.png");
    }

    // Common renderer code
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
