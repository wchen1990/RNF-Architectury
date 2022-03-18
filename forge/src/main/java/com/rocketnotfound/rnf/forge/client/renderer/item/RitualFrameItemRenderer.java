package com.rocketnotfound.rnf.forge.client.renderer.item;

import com.rocketnotfound.rnf.client.model.RitualFrameItemModel;
import com.rocketnotfound.rnf.item.RitualFrameItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RitualFrameItemRenderer extends GeoItemRenderer<RitualFrameItem> {
    public RitualFrameItemRenderer() {
        super(new RitualFrameItemModel());
    }

    @Override
    public RenderLayer getRenderType(RitualFrameItem animatable, float partialTicks, MatrixStack stack,
                                      VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                      Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
