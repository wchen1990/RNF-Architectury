package com.rocketnotfound.rnf.mixin.access;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccess {

    @Accessor
    Set<Block> getBlocks();

    @Accessor
    @Mutable
    void setBlocks(Set<Block> blocks);
}
