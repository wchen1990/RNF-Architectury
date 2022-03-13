package com.rocketnotfound.rnf.blockentity;

import com.mojang.datafixers.types.Type;
import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.mixin.access.BlockEntityTypeBuilderAccess;
import com.rocketnotfound.rnf.util.RegistryObject;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class RNFBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(RNF.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY);

    public static final BlockEntityType<RitualStandBlockEntity> RITUAL_STAND  = register("ritual_stand", BlockEntityType.Builder.of(RitualStandBlockEntity::new, RNFBlocks.RITUAL_STAND.get()));

    private static <T extends BlockEntity> BlockEntityType<T> register(String key, BlockEntityType.Builder<T> builder) {
        if (((BlockEntityTypeBuilderAccess) (Object) builder).getValidBlocks().isEmpty()) {
            RNF.LOG.warn("Block entity type {} requires at least one valid block to be defined!", (Object) key);
        }

        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, key);
        BlockEntityType<T> blockEntityType = builder.build(type);
        BLOCK_ENTITIES.register(key, () -> blockEntityType);
        return blockEntityType;
    }
}
