package com.rocketnotfound.rnf.fabric;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.fabric.client.renderer.blockentity.RitualFrameRenderer;
import com.rocketnotfound.rnf.fabric.client.renderer.blockentity.RitualPrimerRenderer;
import com.rocketnotfound.rnf.fabric.client.renderer.item.RitualFrameItemRenderer;
import com.rocketnotfound.rnf.fabric.client.renderer.item.RitualPrimerItemRenderer;
import com.rocketnotfound.rnf.fabric.client.renderer.item.RitualStaffItemRenderer;
import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.proxy.ClientProxy;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RNFabricClient implements ClientModInitializer {
    @SuppressWarnings({ "unchecked" })
    @Override
    public void onInitializeClient() {
        // Proxy
        RNF.PROXY = new ClientProxy();

        // Blocks
        BlockEntityRendererRegistry.register(RNFBlockEntities.RITUAL_FRAME.get(),
                (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new RitualFrameRenderer());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(RNFBlocks.RITUAL_FRAME.get(), RenderLayer.getCutout());

        BlockEntityRendererRegistry.register(RNFBlockEntities.RITUAL_PRIMER.get(),
                (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new RitualPrimerRenderer());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(RNFBlocks.RITUAL_PRIMER.get(), RenderLayer.getCutout());

        // Set render layers for all TransparentBlocks
        RNFBlocks.RUNE_BLOCKS.forEach((blockRegistry) -> {
            if (blockRegistry.isPresent() && blockRegistry.get() instanceof TransparentBlock) {
                BlockRenderLayerMapImpl.INSTANCE.putBlock(blockRegistry.get(), RenderLayer.getTranslucent());
            }
        });

        //Items
        GeoItemRenderer.registerItemRenderer(RNFItems.RITUAL_FRAME.get(), new RitualFrameItemRenderer());
        GeoItemRenderer.registerItemRenderer(RNFItems.RITUAL_STAFF.get(), new RitualStaffItemRenderer());
        GeoItemRenderer.registerItemRenderer(RNFItems.RITUAL_PRIMER.get(), new RitualPrimerItemRenderer());
    }
}
