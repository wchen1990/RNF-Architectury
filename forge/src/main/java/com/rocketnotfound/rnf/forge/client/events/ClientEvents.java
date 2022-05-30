package com.rocketnotfound.rnf.forge.client.events;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.util.client.MixinBackgroundRendererForFluidsHelper;
import net.minecraft.fluid.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void renderFogForUntaggedFluids(final EntityViewRenderEvent.FogDensity event) {
        if (RNF.serverConfig().MISC.FORCE_UNTAGGED_AS_WATER) {
            FluidState fluidState = MixinBackgroundRendererForFluidsHelper.getNearbyFluid(event.getCamera());
            if (MixinBackgroundRendererForFluidsHelper.matchesCondition(fluidState)) {
                event.setDensity(RNF.serverConfig().MISC.FORCED_FOG_DENSITY);
                event.setCanceled(true);
            }
        }
    }
}
