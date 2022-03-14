package com.rocketnotfound.rnf;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.client.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.item.RNFItems;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RNF {
    public static final String MOD_ID = "rnf";
    public static final String MOD_NAME = "RNF";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static void init() {
        RNFBlocks.BLOCKS.register();
        RNFBlockEntities.BLOCK_ENTITIES.register();
        RNFItems.ITEMS.register();
        RNFParticleTypes.PARTICLES.register();
    }

    public static Identifier createIdentifier(String path) {
        return new Identifier(MOD_ID, path);
    }
}
