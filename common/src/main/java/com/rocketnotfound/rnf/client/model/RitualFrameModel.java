package com.rocketnotfound.rnf.client.model;

import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
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

    // Common renderer code
    public static void render(BlockEntity tile, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn,
                       int combinedLightIn, int combinedOverlayIn) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        Profiler profiler = minecraft.getProfiler();

        profiler.push("common");
        ItemStack stack = ((RitualFrameBlockEntity) tile).getItem();
        if (!stack.isEmpty()) {
            profiler.push("variable");
            int age = (int) (minecraft.world.getTimeOfDay() % 314);
            float rotation = (age + partialTicks) / 25.0F + 6.0F;
            profiler.pop();

            profiler.push("matrix");
            matrixStackIn.push();

            profiler.push("tranform");
            matrixStackIn.translate(0.5,0.5,0.5);
            matrixStackIn.scale(0.32f, 0.32f, 0.32f);
            RitualFrameModel.rotateItem(tile.getCachedState().get(Properties.FACING), matrixStackIn);
            matrixStackIn.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(rotation));
            profiler.pop();

            profiler.push("item_render");
            minecraft.getItemRenderer()
                    .renderItem(stack, ModelTransformation.Mode.GUI, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn, 0);
            profiler.pop();

            matrixStackIn.pop();
            profiler.pop();
        } else {
            // Clear profiler
            profiler.push("variable");
            profiler.pop();

            profiler.push("matrix");

            profiler.push("tranform");
            profiler.pop();

            profiler.push("item_render");
            profiler.pop();

            profiler.pop();
        }
        profiler.pop();
    }

    public static void rotateItem(Direction facing, MatrixStack stack) {
        switch (facing) {
            case SOUTH:
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                break;
            case WEST:
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
                break;
            case NORTH:
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(270));
                break;
            case EAST:
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(270));
                break;
            case UP:
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0));
                break;
            case DOWN:
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                break;
        }
    }

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
