package com.rocketnotfound.rnf.forge;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RNF.MOD_ID)
public class RNForge {
    public RNForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(RNF.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        RNF.init();
    }
}
