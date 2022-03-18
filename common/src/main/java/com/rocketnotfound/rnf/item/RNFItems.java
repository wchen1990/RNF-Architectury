package com.rocketnotfound.rnf.item;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RNFItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(RNF.MOD_ID, Registry.ITEM_KEY);
    public static final ItemGroup CREATIVE_TAB = CreativeTabRegistry.create(new Identifier(RNF.MOD_ID, "rnf_tab"), () ->
            new ItemStack(RNFItems.MOONSTONE.get()));

    public static final RegistrySupplier<Item> MOONSTONE = ITEMS.register("moonstone", () -> new BlockItem(RNFBlocks.MOONSTONE.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> DEEP_MOONSTONE = ITEMS.register("deep_moonstone", () -> new BlockItem(RNFBlocks.DEEP_MOONSTONE.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RITUAL_FRAME = ITEMS.register("ritual_frame", () -> new RitualFrameItem(new Item.Settings().group(CREATIVE_TAB)));
}
