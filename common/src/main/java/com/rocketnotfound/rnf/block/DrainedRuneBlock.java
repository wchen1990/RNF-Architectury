package com.rocketnotfound.rnf.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.TransparentBlock;
import net.minecraft.sound.BlockSoundGroup;


public class DrainedRuneBlock extends TransparentBlock {
    public DrainedRuneBlock() {
        this(
            AbstractBlock.Settings.of(Material.GLASS, MapColor.WHITE_GRAY)
                .nonOpaque()
                .requiresTool()
                .sounds(BlockSoundGroup.SMALL_AMETHYST_BUD)
                .strength(1.5f, 6.0f)
        );
    }

    public DrainedRuneBlock(Settings settings) {
        super(settings);
    }
}
