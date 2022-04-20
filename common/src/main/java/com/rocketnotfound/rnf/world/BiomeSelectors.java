package com.rocketnotfound.rnf.world;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;

import java.util.function.Predicate;

public class BiomeSelectors {
    private BiomeSelectors() {}
    public static BiomeSource overworld = MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(BuiltinRegistries.BIOME);
    public static BiomeSource nether = MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME);
    public static BiomeSource end = new TheEndBiomeSource(BuiltinRegistries.BIOME, 0);

    public static Predicate<BiomeModifications.BiomeContext> foundInSource(BiomeSource sourceIn) {
        return context -> sourceIn.getBiomes().stream().anyMatch(biome -> biome.comp_349().equals(BuiltinRegistries.BIOME.get(context.getKey())));
    }

    @ExpectPlatform
    public static Predicate<BiomeModifications.BiomeContext> foundInOverworld() {
        return foundInSource(overworld);
    }

    @ExpectPlatform
    public static Predicate<BiomeModifications.BiomeContext> foundInNether() {
        return foundInSource(nether);
    }

    @ExpectPlatform
    public static Predicate<BiomeModifications.BiomeContext> foundInEnd() {
        return foundInSource(end);
    }
}