package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

public class RNFBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(RNF.MOD_ID, Registry.BLOCK_KEY);

    public static final RegistrySupplier<Block> MOONSTONE = BLOCKS.register("moonstone", () -> new MoonstoneBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).requiresTool().sounds(BlockSoundGroup.STONE).strength(1.5f, 6.0f)));
    public static final RegistrySupplier<Block> DEEP_MOONSTONE = BLOCKS.register("deep_moonstone", () -> new DeepMoonstoneBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).requiresTool().sounds(BlockSoundGroup.STONE).strength(1.5f, 6.0f)));
    public static final RegistrySupplier<Block> RITUAL_STAND = BLOCKS.register("ritual_stand", () -> new RitualStandBlock(AbstractBlock.Settings.of(Material.STONE, MapColor.TERRACOTTA_WHITE).requiresTool().sounds(BlockSoundGroup.STONE).strength(1.5f, 6.0f)));
}
