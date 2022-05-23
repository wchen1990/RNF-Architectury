package com.rocketnotfound.rnf.world.gen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;

import java.util.Collections;
import java.util.List;

public class RitualFrameProcessor extends StructureProcessor {
    public static final RitualFrameProcessor INSTANCE = new RitualFrameProcessor(Collections.emptyList());
    public static final Codec<RitualFrameProcessor> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(Codec.STRING).fieldOf("loot_table").orElse(Collections.emptyList()).forGetter((ruleTest) -> ruleTest.lootTable)
    ).apply(instance, RitualFrameProcessor::new));
    public static final StructureProcessorType<RitualFrameProcessor> TYPE = () -> CODEC;

    protected final List<String> lootTable;

    public RitualFrameProcessor(List<String> lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public Structure.StructureBlockInfo process(WorldView worldView, BlockPos blockPos, BlockPos blockPos2, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        if (structureBlockInfo2.state.isOf(RNFBlocks.RITUAL_FRAME.get())) {
            BlockEntity be = worldView.getBlockEntity(structureBlockInfo2.pos);
            if (be instanceof RitualFrameBlockEntity rfbe) {
            }
        }
        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
