package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.function.ToIntFunction;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RNFBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(RNF.MOD_ID, Registry.BLOCK_KEY);

    public static final TagKey<Block> MOONSTONE_ORES = TagKey.of(Registry.BLOCK_KEY, createIdentifier("moonstone_ores"));
    public static final RegistrySupplier<Block> MOONSTONE_ORE = BLOCKS.register("moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).requiresTool().ticksRandomly().luminance(litBlockLuminance(9)).sounds(BlockSoundGroup.STONE).strength(1.5f, 6.0f)));
    public static final RegistrySupplier<Block> DEEPSLATE_MOONSTONE_ORE = BLOCKS.register("deepslate_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));

    public static final RegistrySupplier<Block> RITUAL_FRAME = BLOCKS.register("ritual_frame", () -> new RitualFrameBlock());
    public static final RegistrySupplier<Block> RITUAL_TRANSCRIBER = BLOCKS.register("ritual_transcriber", () -> new RitualTranscriberBlock());
    public static final RegistrySupplier<Block> RITUAL_PRIMER = BLOCKS.register("ritual_primer", () -> new RitualPrimerBlock());

    public static final RegistrySupplier<Block> DRAINED_RUNE_BLOCK = BLOCKS.register("drained_rune_block", () -> new DrainedRuneBlock());

    public static final TagKey<Block> ACTIVE_RUNE_BLOCKS = TagKey.of(Registry.BLOCK_KEY, createIdentifier("active_rune_blocks"));

    public static final RegistrySupplier<Block> RUNE_BLOCK = BLOCKS.register("rune_block", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_A = BLOCKS.register("rune_block_a", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_B = BLOCKS.register("rune_block_b", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_C = BLOCKS.register("rune_block_c", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_D = BLOCKS.register("rune_block_d", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_E = BLOCKS.register("rune_block_e", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_F = BLOCKS.register("rune_block_f", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_G = BLOCKS.register("rune_block_g", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_H = BLOCKS.register("rune_block_h", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_I = BLOCKS.register("rune_block_i", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_J = BLOCKS.register("rune_block_j", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_K = BLOCKS.register("rune_block_k", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_L = BLOCKS.register("rune_block_l", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_M = BLOCKS.register("rune_block_m", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_N = BLOCKS.register("rune_block_n", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_O = BLOCKS.register("rune_block_o", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_P = BLOCKS.register("rune_block_p", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_Q = BLOCKS.register("rune_block_q", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_R = BLOCKS.register("rune_block_r", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_S = BLOCKS.register("rune_block_s", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_T = BLOCKS.register("rune_block_t", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_U = BLOCKS.register("rune_block_u", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_V = BLOCKS.register("rune_block_v", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_W = BLOCKS.register("rune_block_w", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_X = BLOCKS.register("rune_block_x", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_Y = BLOCKS.register("rune_block_y", () -> new RuneBlock());
    public static final RegistrySupplier<Block> RUNE_BLOCK_Z = BLOCKS.register("rune_block_z", () -> new RuneBlock());
    public static final List<RegistrySupplier<Block>> RUNE_BLOCKS = List.of(
        DRAINED_RUNE_BLOCK,
        RUNE_BLOCK,
        RUNE_BLOCK_A,
        RUNE_BLOCK_B,
        RUNE_BLOCK_C,
        RUNE_BLOCK_D,
        RUNE_BLOCK_E,
        RUNE_BLOCK_F,
        RUNE_BLOCK_G,
        RUNE_BLOCK_H,
        RUNE_BLOCK_I,
        RUNE_BLOCK_J,
        RUNE_BLOCK_K,
        RUNE_BLOCK_L,
        RUNE_BLOCK_M,
        RUNE_BLOCK_N,
        RUNE_BLOCK_O,
        RUNE_BLOCK_P,
        RUNE_BLOCK_Q,
        RUNE_BLOCK_R,
        RUNE_BLOCK_S,
        RUNE_BLOCK_T,
        RUNE_BLOCK_U,
        RUNE_BLOCK_V,
        RUNE_BLOCK_W,
        RUNE_BLOCK_X,
        RUNE_BLOCK_Y,
        RUNE_BLOCK_Z
    );

    // Register Moonstone Variants for use in compat with Mores and Unearthed
    // Mores variants
    public static final RegistrySupplier<Block> ANDESITE_MOONSTONE_ORE = BLOCKS.register("andesite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> DIORITE_MOONSTONE_ORE = BLOCKS.register("diorite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> GRANITE_MOONSTONE_ORE = BLOCKS.register("granite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> TUFF_MOONSTONE_ORE = BLOCKS.register("tuff_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));

    // Unearthed variants
    public static final RegistrySupplier<Block> BEIGE_LIMESTONE_MOONSTONE_ORE = BLOCKS.register("beige_limestone_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> CONGLOMERATE_MOONSTONE_ORE = BLOCKS.register("conglomerate_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> GABBRO_MOONSTONE_ORE = BLOCKS.register("gabbro_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> GRANDIORITE_MOONSTONE_ORE = BLOCKS.register("grandiorite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> GREY_LIMESTONE_MOONSTONE_ORE = BLOCKS.register("grey_limestone_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> KIMBERLITE_MOONSTONE_ORE = BLOCKS.register("kimberlite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> LIMESTONE_MOONSTONE_ORE = BLOCKS.register("limestone_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> MUDSTONE_MOONSTONE_ORE = BLOCKS.register("mudstone_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> PHYLLITE_MOONSTONE_ORE = BLOCKS.register("phyllite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> QUARTZITE_MOONSTONE_ORE = BLOCKS.register("quartzite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> RHYOLITE_MOONSTONE_ORE = BLOCKS.register("rhyolite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> SILTSTONE_MOONSTONE_ORE = BLOCKS.register("siltstone_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> SLATE_MOONSTONE_ORE = BLOCKS.register("slate_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));
    public static final RegistrySupplier<Block> WHITE_GRANITE_MOONSTONE_ORE = BLOCKS.register("white_granite_moonstone_ore", () -> new MoonstoneOreBlock(AbstractBlock.Settings.copy(MOONSTONE_ORE.get())));

    public static ToIntFunction<BlockState> blockLuminance(int luminance) {
        return (blockState) -> luminance;
    }

    public static ToIntFunction<BlockState> litBlockLuminance(int luminance) {
        return (blockState) -> blockState.get(Properties.LIT) ? luminance : 0;
    }

    public static ToIntFunction<BlockState> blockLuminanceWithProperty(int luminance, BooleanProperty property, int defaultValue) {
        return (blockState) -> blockState.get(property) ? luminance : defaultValue;
    }
}
