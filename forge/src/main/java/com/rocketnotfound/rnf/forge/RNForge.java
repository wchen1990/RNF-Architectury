package com.rocketnotfound.rnf.forge;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.compat.forge.curios.CuriosCompat;
import com.rocketnotfound.rnf.compat.forge.unearthed.UECompat;
import com.rocketnotfound.rnf.forge.item.ForgeRitualFrameItem;
import com.rocketnotfound.rnf.forge.item.ForgeRitualPrimerItem;
import com.rocketnotfound.rnf.forge.item.ForgeRitualStaffItem;
import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.proxy.ClientProxy;
import com.rocketnotfound.rnf.proxy.ServerProxy;
import com.rocketnotfound.rnf.world.gen.feature.RNFFeatures;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RNF.MOD_ID)
public class RNForge {
    public RNForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(RNF.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // There has to be a better way
        RNFItems.ITEMS.register("ritual_frame", () -> new ForgeRitualFrameItem(new Item.Settings().group(RNFItems.CREATIVE_TAB)));
        RNFItems.ITEMS.register("ritual_staff", () -> new ForgeRitualStaffItem(new Item.Settings().group(RNFItems.CREATIVE_TAB)));
        RNFItems.ITEMS.register("ritual_primer", () -> new ForgeRitualPrimerItem(new Item.Settings().group(RNFItems.CREATIVE_TAB)));

        RNF.PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

        RNF.init();

        IEventBus bus = EventBuses.getModEventBus(RNF.MOD_ID).get();
        bus.addListener(this::commonSetup);

        if (ModList.get().isLoaded("unearthed")) {
            MinecraftForge.EVENT_BUS.register(new UECompat());
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        RNFFeatures.init();
        if (ModList.get().isLoaded("mores")) {
            RNFFeatures.initMores();
        }
        if (ModList.get().isLoaded("curios")) {
            CuriosCompat.init();
        }
    }
}
