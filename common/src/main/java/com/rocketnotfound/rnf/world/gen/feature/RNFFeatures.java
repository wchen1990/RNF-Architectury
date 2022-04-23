package com.rocketnotfound.rnf.world.gen.feature;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.world.BiomeSelectors;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import java.util.Arrays;
import java.util.List;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RNFFeatures {
    private static final List<OreFeatureConfig.Target> MOONSTONE_ORES_NORMAL = List.of(
        OreFeatureConfig.createTarget(
            OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
            RNFBlocks.MOONSTONE_ORE.get().getDefaultState()
        ),
        OreFeatureConfig.createTarget(
            OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES,
            RNFBlocks.DEEPSLATE_MOONSTONE_ORE.get().getDefaultState()
        )
    );
    private static final List<OreFeatureConfig.Target> MOONSTONE_ORES_MORES = List.of(
        OreFeatureConfig.createTarget(
            new BlockMatchRuleTest(Blocks.ANDESITE),
            RNFBlocks.ANDESITE_MOONSTONE_ORE.get().getDefaultState()
        ),
        OreFeatureConfig.createTarget(
            new BlockMatchRuleTest(Blocks.DIORITE),
            RNFBlocks.DIORITE_MOONSTONE_ORE.get().getDefaultState()
        ),
        OreFeatureConfig.createTarget(
            new BlockMatchRuleTest(Blocks.GRANITE),
            RNFBlocks.GRANITE_MOONSTONE_ORE.get().getDefaultState()
        ),
        OreFeatureConfig.createTarget(
            new BlockMatchRuleTest(Blocks.TUFF),
            RNFBlocks.TUFF_MOONSTONE_ORE.get().getDefaultState()
        )
    );

    private static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> ORE_MOONSTONE = ConfiguredFeatures.register(
        "ore_moonstone",
        Feature.ORE,
        new OreFeatureConfig(MOONSTONE_ORES_NORMAL, RNF.serverConfig().WORLD_GEN.MOONSTONE_ORE_VEIN_SIZE)
    );
    private static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> MORES_MOONSTONE = ConfiguredFeatures.register(
        "mores_moonstone",
        Feature.ORE,
        new OreFeatureConfig(MOONSTONE_ORES_MORES, RNF.serverConfig().WORLD_GEN.MOONSTONE_ORE_VEIN_SIZE)
    );

    public static PlacedFeature PLACED_ORE_MOONSTONE = new PlacedFeature(
        RegistryEntry.of(ORE_MOONSTONE.comp_349()),
        Arrays.asList(
            CountPlacementModifier.of(RNF.serverConfig().WORLD_GEN.MOONSTONE_VEINS_PER_CHUNK), // number of veins per chunk
            SquarePlacementModifier.of(), // spreading horizontally
            HeightRangePlacementModifier.uniform(YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MIN_Y), YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MAX_Y)),
            BiomePlacementModifier.of()
        )
    );
    public static PlacedFeature PLACED_MORES_MOONSTONE = new PlacedFeature(
        RegistryEntry.of(MORES_MOONSTONE.comp_349()),
        Arrays.asList(
            CountPlacementModifier.of(RNF.serverConfig().WORLD_GEN.MOONSTONE_VEINS_PER_CHUNK), // number of veins per chunk
            SquarePlacementModifier.of(), // spreading horizontally
            HeightRangePlacementModifier.uniform(YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MIN_Y), YOffset.fixed(RNF.serverConfig().WORLD_GEN.MOONSTONE_SPAWN_MAX_Y)),
            BiomePlacementModifier.of()
        )
    );

    public static void init() {
        PlacedFeature moonstone = Registry.register(
            BuiltinRegistries.PLACED_FEATURE,
            createIdentifier("overworld_moonstone_gen"),
            PLACED_ORE_MOONSTONE
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
    }

    public static void initMores() {
        PlacedFeature moonstone = Registry.register(
            BuiltinRegistries.PLACED_FEATURE,
            createIdentifier("mores_overworld_moonstone_gen"),
            PLACED_MORES_MOONSTONE
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
    }
}
