package com.rocketnotfound.rnf.forge.client.renderer.item;

import com.rocketnotfound.rnf.client.model.RitualStaffItemModel;
import com.rocketnotfound.rnf.item.RitualStaffItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RitualStaffItemRenderer extends GeoItemRenderer<RitualStaffItem> {
    public RitualStaffItemRenderer() {
        super(new RitualStaffItemModel());
    }

    @Override
    public RenderLayer getRenderType(RitualStaffItem animatable, float partialTicks, MatrixStack stack,
                                     VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                                     Identifier textureLocation) {
        return RenderLayer.getEntityTranslucent(textureLocation);
    }
}
