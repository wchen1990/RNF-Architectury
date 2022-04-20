package com.rocketnotfound.rnf.world.forge;

import com.rocketnotfound.rnf.world.BiomeSelectors;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraftforge.common.BiomeDictionary;

import java.util.function.Predicate;

public class BiomeSelectorsImpl {
    public static Predicate<BiomeModifications.BiomeContext> foundInOverworld() {
        return biomeContext -> BiomeDictionary.hasType(RegistryKey.of(Registry.BIOME_KEY, biomeContext.getKey()), BiomeDictionary.Type.OVERWORLD) || BiomeSelectors.foundInSource(BiomeSelectors.overworld).test(biomeContext);
    }

    public static Predicate<BiomeModifications.BiomeContext> foundInNether() {
        return biomeContext -> BiomeDictionary.hasType(RegistryKey.of(Registry.BIOME_KEY, biomeContext.getKey()), BiomeDictionary.Type.NETHER) || BiomeSelectors.foundInSource(BiomeSelectors.nether).test(biomeContext);
    }

    public static Predicate<BiomeModifications.BiomeContext> foundInEnd() {
        return  biomeContext -> BiomeDictionary.hasType(RegistryKey.of(Registry.BIOME_KEY, biomeContext.getKey()), BiomeDictionary.Type.END) || BiomeSelectors.foundInSource(BiomeSelectors.end).test(biomeContext);
    }
}