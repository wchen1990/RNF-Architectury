package com.rocketnotfound.rnf.block;

import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;

import static com.rocketnotfound.rnf.block.RNFBlocks.blockContextFalse;


public class DrainedRuneBlock extends AbstractGlassBlock {
    public DrainedRuneBlock() {
        this(
            AbstractBlock.Settings.of(Material.GLASS, MapColor.WHITE_GRAY)
                .nonOpaque()
                .suffocates(blockContextFalse())
                .blockVision(blockContextFalse())
                .sounds(BlockSoundGroup.SMALL_AMETHYST_BUD)
                .strength(1.5f, 6.0f)
        );
    }

    public DrainedRuneBlock(Settings settings) {
        super(settings);
    }
}
