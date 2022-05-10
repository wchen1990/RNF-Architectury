package com.rocketnotfound.rnf.world.gen.processor;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.registry.Registry;

public class RNFProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> PROCESSORS = DeferredRegister.create(RNF.MOD_ID, Registry.STRUCTURE_PROCESSOR_KEY);

    public static final RegistrySupplier<StructureProcessorType<?>> CHEST_PROCESSOR = PROCESSORS.register("chest_processor", () -> ChestLootTableProcessor.TYPE);
    public static final RegistrySupplier<StructureProcessorType<?>> FLUID_PROCESSOR = PROCESSORS.register("fluid_processor", () -> FluidProcessor.TYPE);
    public static final RegistrySupplier<StructureProcessorType<?>> REDSTONE_PROCESSOR = PROCESSORS.register("redstone_processor", () -> RedstoneProcessor.TYPE);
    public static final RegistrySupplier<StructureProcessorType<?>> RANDOMLY_REPLACE_BLOCK_PROCESSOR = PROCESSORS.register("randomly_replace_block_processor", () -> RandomlyReplaceBlockProcessor.TYPE);
}
