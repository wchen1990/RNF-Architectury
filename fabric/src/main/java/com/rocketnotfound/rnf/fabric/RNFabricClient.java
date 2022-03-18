package com.rocketnotfound.rnf.fabric;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.fabric.client.renderer.blockentity.RitualFrameRenderer;
import com.rocketnotfound.rnf.fabric.client.renderer.item.RitualFrameItemRenderer;
import com.rocketnotfound.rnf.item.RNFItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RNFabricClient implements ClientModInitializer {
    @SuppressWarnings({ "unchecked" })
    @Override
    public void onInitializeClient() {
        // Blocks
        BlockEntityRendererRegistry.register(RNFBlockEntities.RITUAL_FRAME.get(),
                (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new RitualFrameRenderer());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(RNFBlocks.RITUAL_FRAME.get(), RenderLayer.getCutout());

        //Items
        GeoItemRenderer.registerItemRenderer(RNFItems.RITUAL_FRAME.get(), new RitualFrameItemRenderer());
    }
}
