package com.rocketnotfound.rnf.mixin.access;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockStateProviderType.class)
public interface BlockStateProviderAccess {

    @Invoker("<init>")
    static <P extends BlockStateProvider> BlockStateProviderType<P> create(Codec<P> codec) {
        throw new Error("Mixin did not apply!");
    }
}
