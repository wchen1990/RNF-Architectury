package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.registry.Registry;

import java.util.function.ToIntFunction;

public class RNFBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(RNF.MOD_ID, Registry.BLOCK_KEY);

    public static final RegistrySupplier<Block> MOONSTONE = BLOCKS.register("moonstone", () -> new MoonstoneBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).requiresTool().ticksRandomly().luminance(litBlockLuminance(9)).sounds(BlockSoundGroup.STONE).strength(1.5f, 6.0f)));
    public static final RegistrySupplier<Block> DEEP_MOONSTONE = BLOCKS.register("deep_moonstone", () -> new MoonstoneBlock(AbstractBlock.Settings.copy(MOONSTONE.get()).mapColor(MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.DEEPSLATE)));
    public static final RegistrySupplier<Block> RITUAL_FRAME = BLOCKS.register("ritual_frame", () -> new RitualFrameBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).nonOpaque().requiresTool().luminance(blockLuminance(6)).sounds(BlockSoundGroup.STONE).strength(1.5f, 6.0f)));

    private static ToIntFunction<BlockState> blockLuminance(int luminance) {
        return (blockState) -> {
            return luminance;
        };
    }

    private static ToIntFunction<BlockState> litBlockLuminance(int luminance) {
        return (blockState) -> {
            return blockState.get(Properties.LIT) ? luminance : 0;
        };
    }
}
