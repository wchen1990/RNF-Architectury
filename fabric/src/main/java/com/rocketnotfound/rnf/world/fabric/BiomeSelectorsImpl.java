package com.rocketnotfound.rnf.world.fabric;

import com.rocketnotfound.rnf.world.BiomeSelectors;
import dev.architectury.registry.level.biome.BiomeModifications;

import java.util.function.Predicate;

import static com.rocketnotfound.rnf.world.BiomeSelectors.foundInSource;

public class BiomeSelectorsImpl {
    public static Predicate<BiomeModifications.BiomeContext> foundInOverworld() {
        return foundInSource(BiomeSelectors.overworld);
    }

    public static Predicate<BiomeModifications.BiomeContext> foundInNether() {
        return foundInSource(BiomeSelectors.nether);
    }

    public static Predicate<BiomeModifications.BiomeContext> foundInEnd() {
        return foundInSource(BiomeSelectors.end);
    }
}