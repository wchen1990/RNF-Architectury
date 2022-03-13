package com.rocketnotfound.rnf;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.client.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.item.RNFItems;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RNF {
    public static final String MOD_ID = "rnf";
    public static final String MOD_NAME = "RNF";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static void init() {
        RNFBlocks.BLOCKS.register();
        RNFItems.ITEMS.register();
        RNFParticleTypes.PARTICLES.register();
    }

    public static ResourceLocation createLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
