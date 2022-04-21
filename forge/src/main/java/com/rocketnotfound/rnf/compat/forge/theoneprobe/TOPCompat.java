package com.rocketnotfound.rnf.compat.forge.theoneprobe;

import com.rocketnotfound.rnf.RNF;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = RNF.MOD_ID)
@SuppressWarnings("unused")
public class TOPCompat {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            if (ModList.get().isLoaded("theoneprobe")) {
                RNF.LOG.info("TOP compat loaded");
                event.getGenerator().addProvider(new TOPRecipeGenerator(event.getGenerator()));
            }
        }
    }

    @SubscribeEvent
    public static void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        if (ModList.get().isLoaded("theoneprobe")) {
            event.getRegistry().registerAll(
                name(TOPRecipe.SERIALIZER, "theoneprobecompat")
            );
        }
    }

    private static <T extends IForgeRegistryEntry<? extends T>> T name(T entry, String name) {
        return entry.setRegistryName(createIdentifier(name));
    }
}
