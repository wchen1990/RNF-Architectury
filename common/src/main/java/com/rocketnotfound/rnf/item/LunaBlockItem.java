package com.rocketnotfound.rnf.item;

import com.rocketnotfound.rnf.block.RNFBlocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Wearable;

import static com.rocketnotfound.rnf.item.RNFItems.CREATIVE_TAB;

public class LunaBlockItem extends BlockItem implements Wearable {
    public LunaBlockItem() {
        super(RNFBlocks.LUNA_BLOCK.get(), new Item.Settings().group(CREATIVE_TAB));
    }

    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }
}
