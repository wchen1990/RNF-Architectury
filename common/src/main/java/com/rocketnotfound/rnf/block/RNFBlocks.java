package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.util.RegistryObject;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class RNFBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(RNF.MOD_ID, Registry.BLOCK_REGISTRY);

    public static final Block MOONSTONE = createBlock(new MoonstoneBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().sound(SoundType.STONE).strength(1.5f, 6.0f)), "moonstone");
    public static final Block DEEP_MOONSTONE = createBlock(new DeepMoonstoneBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().sound(SoundType.STONE).strength(1.5f, 6.0f)), "deep_moonstone");
    public static final Block RITUAL_STAND = createBlock(new RitualStandBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().sound(SoundType.STONE).strength(1.5f, 6.0f)), "ritual_stand");

    public static Block createBlock(Block block, String id) {
        BLOCKS.register(id, () -> block);
        return block;
    }
}
