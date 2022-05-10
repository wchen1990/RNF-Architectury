package com.rocketnotfound.rnf.world.gen.processor;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.tick.OrderedTick;

public class RedstoneProcessor extends StructureProcessor {
    public static final RedstoneProcessor INSTANCE = new RedstoneProcessor();
    public static final Codec<RedstoneProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public static final StructureProcessorType<RedstoneProcessor> TYPE = () -> CODEC;

    public RedstoneProcessor() {}

    @Override
    public Structure.StructureBlockInfo process(WorldView worldView, BlockPos blockPos, BlockPos blockPos2, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        if (structureBlockInfo2.state.getBlock() == Blocks.REDSTONE_WIRE) {
            tickRedstone(worldView, structureBlockInfo2);
        }
        return structureBlockInfo2;
    }

    protected void tickRedstone(WorldView worldView, Structure.StructureBlockInfo structureBlockInfoWorld) {
        Chunk currentChunk = worldView.getChunk(structureBlockInfoWorld.pos);
        currentChunk.getBlockTickScheduler().scheduleTick(OrderedTick.create(Blocks.REDSTONE_WIRE, structureBlockInfoWorld.pos));
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
