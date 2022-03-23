package com.rocketnotfound.rnf.fabric.client.renderer.blockentity;

import com.rocketnotfound.rnf.block.RitualFrameBlock;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.client.model.RitualFrameModel;
import com.rocketnotfound.rnf.client.render.RNFRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RitualFrameRenderer extends GeoBlockRenderer<RitualFrameBlockEntity> {
    public RitualFrameRenderer() {
        super(new RitualFrameModel());
    }

    @Override
    public void render(BlockEntity tile, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn,
                       int combinedLightIn, int combinedOverlayIn) {
        World world = tile.getWorld();
        BlockState state = world.getBlockState(tile.getPos());
        if (!(state.getBlock() instanceof RitualFrameBlock)) return;

        super.render(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        RitualFrameModel.render(tile, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }

    @Override
    public RenderLayer getRenderType(RitualFrameBlockEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation) {
        return RNFRenderLayer.getTranslucentRNF(getTextureLocation(animatable));
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack stack) {
        RitualFrameModel.rotateBlock(facing, stack);
    }
}
