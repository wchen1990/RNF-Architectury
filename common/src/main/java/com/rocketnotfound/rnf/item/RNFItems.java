package com.rocketnotfound.rnf.item;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.util.RegistryObject;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class RNFItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(RNF.MOD_ID, Registry.ITEM_REGISTRY);
    public static final CreativeModeTab CREATIVE_TAB = CreativeTabRegistry.create(new ResourceLocation(RNF.MOD_ID, "rnf_tab"), () ->
            new ItemStack(RNFItems.MOONSTONE.get()));

    public static final RegistrySupplier<Item> MOONSTONE = ITEMS.register("moonstone", () -> new BlockItem(RNFBlocks.MOONSTONE.get(), new Item.Properties().tab(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> DEEP_MOONSTONE = ITEMS.register("deep_moonstone", () -> new BlockItem(RNFBlocks.DEEP_MOONSTONE.get(), new Item.Properties().tab(CREATIVE_TAB)));
}
