package com.rocketnotfound.rnf.world.gen.structures;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.StructureFeature;

public class RNFStructures {
    public static final DeferredRegister<StructureFeature<?>> STRUCTURES = DeferredRegister.create(RNF.MOD_ID, Registry.STRUCTURE_FEATURE_KEY);

    public static final RegistrySupplier<SkyStructure> SKY_STRUCTURE = STRUCTURES.register("sky_structure", SkyStructure::new);
}
