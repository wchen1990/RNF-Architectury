package com.rocketnotfound.rnf.forge;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.forge.item.ForgeRitualFrameItem;
import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.item.RitualFrameItem;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RNF.MOD_ID)
public class RNForge {
    public RNForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(RNF.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // There has to be a better way
        RNFItems.ITEMS.register("ritual_frame", () -> new ForgeRitualFrameItem(new Item.Settings().group(RNFItems.CREATIVE_TAB)));

        RNF.init();
    }
}
