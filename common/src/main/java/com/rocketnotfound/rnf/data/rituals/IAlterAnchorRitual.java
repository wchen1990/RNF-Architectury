package com.rocketnotfound.rnf.data.rituals;

import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Pair;

public interface IAlterAnchorRitual {
    Block alterAnchor(Inventory inventory);
    Pair<Block, String> getAnchor();
    Pair<String, String> getAnchorStrings();
}
