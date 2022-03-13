package com.rocketnotfound.rnf.block;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.util.RegistryObject;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
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

    public static final RegistrySupplier<Block> MOONSTONE = BLOCKS.register("moonstone", () -> new MoonstoneBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().sound(SoundType.STONE).strength(1.5f, 6.0f)));
    public static final RegistrySupplier<Block> DEEP_MOONSTONE = BLOCKS.register("deep_moonstone", () -> new DeepMoonstoneBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().sound(SoundType.STONE).strength(1.5f, 6.0f)));
    public static final RegistrySupplier<Block> RITUAL_STAND = BLOCKS.register("ritual_stand", () -> new RitualStandBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops().sound(SoundType.STONE).strength(1.5f, 6.0f)));
}
