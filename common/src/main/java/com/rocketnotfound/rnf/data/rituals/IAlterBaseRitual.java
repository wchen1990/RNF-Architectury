package com.rocketnotfound.rnf.data.rituals;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Pair;

public interface IAlterBaseRitual {
    Block alterBase(Inventory inventory);
    Pair<Block, String> getBase();
    Pair<String, String> getBaseStrings();
}
