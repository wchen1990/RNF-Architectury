package com.rocketnotfound.rnf.fabric;

import com.rocketnotfound.rnf.RNF;
import net.fabricmc.api.ModInitializer;

public class RNFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RNF.init();
    }
}
