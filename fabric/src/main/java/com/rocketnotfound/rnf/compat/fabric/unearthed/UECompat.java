package com.rocketnotfound.rnf.compat.fabric.unearthed;

import com.rocketnotfound.rnf.block.RNFBlocks;
import lilypuree.hyle.compat.StoneTypeCallback;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class UECompat {
    public static void init() {
        StoneTypeCallback.EVENT.register((stoneType) -> {
            stoneType.getBaseBlock();
            Optional<Block> block = Registry.BLOCK.getOrEmpty(
                createIdentifier(
                    String.format(
                        "%s_moonstone_ore",
                        Registry.BLOCK.getId(stoneType.getBaseBlock().getBlock()).getPath()
                    )
                )
            );

            if (block.isPresent()) {
                stoneType.getOreMap().put(
                    RNFBlocks.MOONSTONE_ORE.get(),
                    block.get().getDefaultState()
                );

                stoneType.getOreMap().put(
                    RNFBlocks.DEEPSLATE_MOONSTONE_ORE.get(),
                    block.get().getDefaultState()
                );
            }

            return true;
        });
    }
}
