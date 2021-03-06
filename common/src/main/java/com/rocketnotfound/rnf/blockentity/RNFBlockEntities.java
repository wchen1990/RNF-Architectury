package com.rocketnotfound.rnf.blockentity;

import com.mojang.datafixers.types.Type;
import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.mixin.access.BlockEntityTypeBuilderAccess;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class RNFBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(RNF.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY);

    public static final RegistrySupplier<BlockEntityType<RitualFrameBlockEntity>> RITUAL_FRAME = BLOCK_ENTITIES.register("ritual_frame", () -> buildType("ritual_frame", BlockEntityType.Builder.create(RitualFrameBlockEntity::new, RNFBlocks.RITUAL_FRAME.get())));
    public static final RegistrySupplier<BlockEntityType<RitualTranscriberBlockEntity>> RITUAL_TRANSCRIBER = BLOCK_ENTITIES.register("ritual_transcriber", () -> buildType("ritual_transcriber", BlockEntityType.Builder.create(RitualTranscriberBlockEntity::new, RNFBlocks.RITUAL_TRANSCRIBER.get())));
    public static final RegistrySupplier<BlockEntityType<RitualPrimerBlockEntity>> RITUAL_PRIMER = BLOCK_ENTITIES.register("ritual_primer", () -> buildType("ritual_primer", BlockEntityType.Builder.create(RitualPrimerBlockEntity::new, RNFBlocks.RITUAL_PRIMER.get())));

    private static <T extends BlockEntity> BlockEntityType<T> buildType(String key, BlockEntityType.Builder<T> builder) {
        if (((BlockEntityTypeBuilderAccess) (Object) builder).getBlocks().isEmpty()) {
            RNF.LOG.warn("Block entity type {} requires at least one valid block to be defined!", key);
        }

        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, key);
        BlockEntityType<T> blockEntityType = builder.build(type);
        return blockEntityType;
    }
}
