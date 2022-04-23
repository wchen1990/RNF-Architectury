package com.rocketnotfound.rnf.fabric;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.compat.fabric.unearthed.UECompat;
import com.rocketnotfound.rnf.proxy.ServerProxy;
import com.rocketnotfound.rnf.world.gen.feature.RNFFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class RNFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RNF.PROXY = new ServerProxy();
        RNF.init();

        RNFFeatures.init();
        if (FabricLoader.getInstance().isModLoaded("mores")) {
            RNFFeatures.initMores();
        }
        if (FabricLoader.getInstance().isModLoaded("unearthed")) {
            UECompat.init();
        }
    }
}
