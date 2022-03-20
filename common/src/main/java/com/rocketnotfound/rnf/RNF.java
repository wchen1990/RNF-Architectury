package com.rocketnotfound.rnf;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RNFBlockEntities;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.proxy.IProxy;
import com.rocketnotfound.rnf.proxy.ServerProxy;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RNF {
    public static final String MOD_ID = "rnf";
    public static final String MOD_NAME = "RNF";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);
    public static IProxy PROXY;

    public static void init() {
        if (PROXY == null) PROXY = new ServerProxy();
        RNFBlocks.BLOCKS.register();
        RNFBlockEntities.BLOCK_ENTITIES.register();
        RNFItems.ITEMS.register();
        RNFParticleTypes.PARTICLES.register();
    }

    public static Identifier createIdentifier(String path) {
        return new Identifier(MOD_ID, path);
    }
}
