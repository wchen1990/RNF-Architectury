package com.rocketnotfound.rnf.fabric;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.fabric.client.renderer.blockentity.RitualFrameRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class RNFabricClient implements ClientModInitializer {
    @SuppressWarnings({ "unchecked" })
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(RNFBlockEntities.RITUAL_FRAME.get(),
                (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new RitualFrameRenderer());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(RNFBlocks.RITUAL_FRAME.get(), RenderLayer.getCutout());
    }
}
