package com.rocketnotfound.rnf.fabric;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.proxy.ServerProxy;
import net.fabricmc.api.ModInitializer;

public class RNFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RNF.PROXY = new ServerProxy();
        RNF.init();
    }
}
