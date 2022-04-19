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
import net.minecraft.tag.TagKey;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RNFItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(RNF.MOD_ID, Registry.ITEM_KEY);
    public static final ItemGroup CREATIVE_TAB = CreativeTabRegistry.create(new Identifier(RNF.MOD_ID, "rnf_tab"), () ->
            new ItemStack(RNFItems.LUNA.get()));

    public static final RegistrySupplier<Item> MOONSTONE_ORE = ITEMS.register("moonstone_ore", () -> new BlockItem(RNFBlocks.MOONSTONE_ORE.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> DEEPSLATE_MOONSTONE_ORE = ITEMS.register("deepslate_moonstone_ore", () -> new BlockItem(RNFBlocks.DEEPSLATE_MOONSTONE_ORE.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> LUNA = ITEMS.register("luna", () -> new Item(new Item.Settings().group(CREATIVE_TAB)));

    public static final RegistrySupplier<Item> RITUAL_FRAME = ITEMS.register("ritual_frame", () -> new RitualFrameItem(new Item.Settings().maxCount(1).group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RITUAL_STAFF = ITEMS.register("ritual_staff", () -> new RitualStaffItem(new Item.Settings().maxCount(1).group(CREATIVE_TAB)));

    public static final RegistrySupplier<Item> RITUAL_TRANSCRIBER = ITEMS.register("ritual_transcriber", () -> new BlockItem(RNFBlocks.RITUAL_TRANSCRIBER.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RITUAL_PRIMER = ITEMS.register("ritual_primer", () -> new RitualPrimerItem(new Item.Settings().group(CREATIVE_TAB)));

    public static final RegistrySupplier<Item> DRAINED_RUNE_BLOCK = ITEMS.register("drained_rune_block", () -> new BlockItem(RNFBlocks.DRAINED_RUNE_BLOCK.get(), new Item.Settings().group(CREATIVE_TAB)));

    public static final TagKey<Item> ACTIVE_RUNE_BLOCKS = TagKey.of(Registry.ITEM_KEY, createIdentifier("active_rune_blocks"));

    public static final RegistrySupplier<Item> RUNE_BLOCK = ITEMS.register("rune_block", () -> new BlockItem(RNFBlocks.RUNE_BLOCK.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_A = ITEMS.register("rune_block_a", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_A.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_B = ITEMS.register("rune_block_b", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_B.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_C = ITEMS.register("rune_block_c", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_C.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_D = ITEMS.register("rune_block_d", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_D.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_E = ITEMS.register("rune_block_e", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_E.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_F = ITEMS.register("rune_block_f", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_F.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_G = ITEMS.register("rune_block_g", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_G.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_H = ITEMS.register("rune_block_h", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_H.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_I = ITEMS.register("rune_block_i", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_I.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_J = ITEMS.register("rune_block_j", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_J.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_K = ITEMS.register("rune_block_k", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_K.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_L = ITEMS.register("rune_block_l", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_L.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_M = ITEMS.register("rune_block_m", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_M.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_N = ITEMS.register("rune_block_n", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_N.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_O = ITEMS.register("rune_block_o", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_O.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_P = ITEMS.register("rune_block_p", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_P.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_Q = ITEMS.register("rune_block_q", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_Q.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_R = ITEMS.register("rune_block_r", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_R.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_S = ITEMS.register("rune_block_s", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_S.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_T = ITEMS.register("rune_block_t", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_T.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_U = ITEMS.register("rune_block_u", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_U.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_V = ITEMS.register("rune_block_v", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_V.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_W = ITEMS.register("rune_block_w", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_W.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_X = ITEMS.register("rune_block_x", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_X.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_Y = ITEMS.register("rune_block_y", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_Y.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> RUNE_BLOCK_Z = ITEMS.register("rune_block_z", () -> new BlockItem(RNFBlocks.RUNE_BLOCK_Z.get(), new Item.Settings().group(CREATIVE_TAB)));
    public static final List<RegistrySupplier<Item>> RUNE_BLOCKS = List.of(
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

    public static String customName(ItemStack itemStack) {
        return itemStack.hasCustomName() ? itemStack.getName().getString() : new TranslatableText(itemStack.getItem().getTranslationKey()).getString();
    }
}
