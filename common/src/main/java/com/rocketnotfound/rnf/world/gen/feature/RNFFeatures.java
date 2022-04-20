package com.rocketnotfound.rnf.world.gen.feature;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.world.BiomeSelectors;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.Arrays;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RNFFeatures {
    private static final ConfiguredFeature<?, ?> OVERWORLD_ORE_MOONSTONE = new ConfiguredFeature(
        Feature.ORE,
        new OreFeatureConfig(
            OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
            RNFBlocks.MOONSTONE_ORE.get().getDefaultState(),
            RNF.serverConfig().WORLD_GEN.MOONSTONE_ORE_VEIN_SIZE
        )
    );
    public static PlacedFeature ORE_MOONSTONE = new PlacedFeature(
        RegistryEntry.of(OVERWORLD_ORE_MOONSTONE),
        Arrays.asList(
            CountPlacementModifier.of(RNF.serverConfig().WORLD_GEN.MOONSTONE_VEINS_PER_CHUNK), // number of veins per chunk
            SquarePlacementModifier.of(), // spreading horizontally
            HeightRangePlacementModifier.uniform(YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MIN_Y), YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MAX_Y))
        ));

    private static final ConfiguredFeature<?, ?> OVERWORLD_ORE_DEEPSLATE_MOONSTONE = new ConfiguredFeature(
        Feature.ORE,
        new OreFeatureConfig(
            OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES,
            RNFBlocks.DEEPSLATE_MOONSTONE_ORE.get().getDefaultState(),
            RNF.serverConfig().WORLD_GEN.MOONSTONE_ORE_VEIN_SIZE
        )
    );
    public static PlacedFeature ORE_DEEPSLATE_MOONSTONE = new PlacedFeature(
        RegistryEntry.of(OVERWORLD_ORE_DEEPSLATE_MOONSTONE),
        Arrays.asList(
            CountPlacementModifier.of(RNF.serverConfig().WORLD_GEN.MOONSTONE_VEINS_PER_CHUNK), // number of veins per chunk
            SquarePlacementModifier.of(), // spreading horizontally
            HeightRangePlacementModifier.uniform(YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MIN_Y), YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MAX_Y))
        )
    );

    public static void init() {
        Registry.register(
            BuiltinRegistries.CONFIGURED_FEATURE,
            createIdentifier("overworld_moonstone_gen"),
            OVERWORLD_ORE_MOONSTONE
        );
        PlacedFeature moonstone = Registry.register(
            BuiltinRegistries.PLACED_FEATURE,
            createIdentifier("overworld_moonstone_gen"),
            ORE_MOONSTONE
        );
        BiomeModifications.addProperties(
            BiomeSelectors.foundInOverworld(),
            (ctx, mutable) -> {
                mutable.getGenerationProperties().addFeature(
                    GenerationStep.Feature.UNDERGROUND_ORES,
                    moonstone
                );
            }
        );

        Registry.register(
            BuiltinRegistries.CONFIGURED_FEATURE,
            createIdentifier("overworld_deepslate_moonstone_gen"),
            OVERWORLD_ORE_DEEPSLATE_MOONSTONE
        );
        PlacedFeature deepslateMoonstone = Registry.register(
            BuiltinRegistries.PLACED_FEATURE,
            createIdentifier("overworld_deepslate_moonstone_gen"),
            ORE_DEEPSLATE_MOONSTONE
        );
        BiomeModifications.addProperties(
            BiomeSelectors.foundInOverworld(),
            (ctx, mutable) -> {
                mutable.getGenerationProperties().addFeature(
                    GenerationStep.Feature.UNDERGROUND_ORES,
                    deepslateMoonstone
                );
            }
        );
    }
}
