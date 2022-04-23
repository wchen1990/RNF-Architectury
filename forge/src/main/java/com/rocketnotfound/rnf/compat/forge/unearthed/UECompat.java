package com.rocketnotfound.rnf.compat.forge.unearthed;

import com.rocketnotfound.rnf.block.RNFBlocks;
import lilypuree.hyle.compat.IStoneType;
import lilypuree.hyle.compat.StoneTypeEvent;
import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class UECompat {
    @SubscribeEvent
    public void stoneCallback(StoneTypeEvent event) {
        IStoneType stoneType = event.getStoneType();
        Block block = ForgeRegistries.BLOCKS.getValue(
            createIdentifier(
                String.format(
                    "%s_moonstone_ore",
                    stoneType.getBaseBlock().getBlock().getRegistryName().getPath()
                )
            )
        );

        if (block != null) {
            stoneType.getOreMap().put(
                RNFBlocks.MOONSTONE_ORE.get(),
                block.getDefaultState()
            );

            stoneType.getOreMap().put(
                RNFBlocks.DEEPSLATE_MOONSTONE_ORE.get(),
                block.getDefaultState()
            );
        }
    }
}
