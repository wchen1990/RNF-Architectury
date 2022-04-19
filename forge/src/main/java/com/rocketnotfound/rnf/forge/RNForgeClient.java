package com.rocketnotfound.rnf.forge;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.block.RuneBlock;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.forge.client.renderer.blockentity.RitualFrameRenderer;
import com.rocketnotfound.rnf.forge.client.renderer.blockentity.RitualPrimerRenderer;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.GeckoLibMod;

@Mod.EventBusSubscriber(modid = RNF.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RNForgeClient {
    @SubscribeEvent
    public static void registerRenderLayers(final FMLClientSetupEvent event) {
        // Set render layers for all TransparentBlocks
        RNFBlocks.RUNE_BLOCKS.forEach((blockRegistry) -> {
            if (blockRegistry.isPresent() && blockRegistry.get() instanceof TransparentBlock) {
                RenderLayers.setRenderLayer(blockRegistry.get(), RenderLayer.getTranslucent());
            }
        });
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        if (!FMLEnvironment.production && !GeckoLibMod.DISABLE_IN_DEV) {
            event.registerBlockEntityRenderer(RNFBlockEntities.RITUAL_FRAME.get(), RitualFrameRenderer::new);
            event.registerBlockEntityRenderer(RNFBlockEntities.RITUAL_PRIMER.get(), RitualPrimerRenderer::new);
        }
    }
}
