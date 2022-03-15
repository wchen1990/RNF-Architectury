package com.rocketnotfound.rnf.forge.client.renderer.blockentity;

import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.client.model.RitualFrameModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RitualFrameRenderer extends GeoBlockRenderer<RitualFrameBlockEntity> {
    public RitualFrameRenderer(BlockEntityRendererFactory.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, new RitualFrameModel());
    }

    @Override
    public RenderLayer getRenderType(RitualFrameBlockEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }
}
