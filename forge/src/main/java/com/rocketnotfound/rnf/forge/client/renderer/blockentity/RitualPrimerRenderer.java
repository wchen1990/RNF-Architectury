package com.rocketnotfound.rnf.forge.client.renderer.blockentity;

import com.rocketnotfound.rnf.blockentity.RitualPrimerBlockEntity;
import com.rocketnotfound.rnf.client.model.RitualPrimerModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RitualPrimerRenderer extends GeoBlockRenderer<RitualPrimerBlockEntity> {
    public RitualPrimerRenderer(BlockEntityRendererFactory.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, new RitualPrimerModel());
    }

    @Override
    public RenderLayer getRenderType(RitualPrimerBlockEntity animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(getTextureLocation(animatable));
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack stack) {
        RitualPrimerModel.rotateBlock(facing, stack);
    }
}
