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
            new ItemStack(RNFItems.MOONSTONE));

    public static final Item MOONSTONE = createItem(RNFBlocks.MOONSTONE);
    public static final Item DEEP_MOONSTONE = createItem(RNFBlocks.DEEP_MOONSTONE);

    public static BlockItem createItem(Block block) {
        return createItem(new BlockItem(block, new Item.Properties().tab(CREATIVE_TAB)), block);
    }

    public static <T extends Item> T createItem(T item, Block block) {
        ResourceLocation id = Registry.BLOCK.getKey(block);
        if (id == null || id.equals(new ResourceLocation("minecraft:air"))) {
            boolean recovered = false;
            for (RegistrySupplier<Block> blockRegistryObject : RNFBlocks.BLOCKS) {
                if (blockRegistryObject.getOrNull() == block) {
                    recovered = true;
                    id = RNF.createLocation(blockRegistryObject.getId().toString());
                    break;
                }
            }
            if (recovered) {
                RNF.LOG.error(String.format("Block \"%s\" was null in the block registry. Using value from RNF's tracked registered blocks...", id.toString()));
            } else {
                throw new IllegalArgumentException("Could not construct item from block using RNF's tracked block list! This should not be possible...");
            }
        }

        return createItem(item, id.getPath());
    }

    public static <T extends Item> T createItem(T item, String id) {
        ITEMS.register(id, () -> item);
        return item;
    }

}
