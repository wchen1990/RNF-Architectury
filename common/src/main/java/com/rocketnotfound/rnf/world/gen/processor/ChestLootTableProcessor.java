package com.rocketnotfound.rnf.world.gen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChestLootTableProcessor extends StructureProcessor {
    public static final ChestLootTableProcessor INSTANCE = new ChestLootTableProcessor(null);
    public static final Codec<ChestLootTableProcessor> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(Codec.STRING).fieldOf("loot_table").orElse(Collections.emptyList()).forGetter((ruleTest) -> ruleTest.lootTable)
    ).apply(instance, ChestLootTableProcessor::new));
    public static final StructureProcessorType<ChestLootTableProcessor> TYPE = () -> CODEC;

    protected final List<String> lootTable;

    public ChestLootTableProcessor(List<String> lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    public Structure.StructureBlockInfo process(WorldView worldView, BlockPos blockPos, BlockPos blockPos2, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        Random random = structurePlacementData.getRandom(structureBlockInfo2.pos);
        if (lootTable != null && lootTable.size() > 0) {
            if (structureBlockInfo2.state.getBlock() == Blocks.CHEST || structureBlockInfo2.state.getBlock() == Blocks.TRAPPED_CHEST) {
                int index = random.nextInt(lootTable.size());
                structureBlockInfo2.nbt.putString("LootTable", lootTable.get(index));
            }
        }
        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
