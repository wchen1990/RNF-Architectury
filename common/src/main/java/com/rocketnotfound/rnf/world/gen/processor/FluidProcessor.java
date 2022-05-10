package com.rocketnotfound.rnf.world.gen.processor;

import com.mojang.serialization.Codec;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.tick.OrderedTick;

public class FluidProcessor extends StructureProcessor {
    public static final FluidProcessor INSTANCE = new FluidProcessor();
    public static final Codec<FluidProcessor> CODEC = Codec.unit(() -> INSTANCE);
    public static final StructureProcessorType<FluidProcessor> TYPE = () -> CODEC;

    public FluidProcessor() {}

    @Override
    public Structure.StructureBlockInfo process(WorldView worldView, BlockPos blockPos, BlockPos blockPos2, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        if (structureBlockInfo2.state.getFluidState().isStill()) {
            tickFluids(worldView, structureBlockInfo2);
        }
        return structureBlockInfo2;
    }

    /* Source: https://github.com/TelepathicGrunt/RepurposedStructures/blob/latest-released/src/main/java/com/telepathicgrunt/repurposedstructures/world/processors/FloodWithWaterProcessor.java */
    protected void tickFluids(WorldView worldView, Structure.StructureBlockInfo structureBlockInfoWorld) {
        Chunk currentChunk = worldView.getChunk(structureBlockInfoWorld.pos);
        FluidState fluidState = structureBlockInfoWorld.state.getFluidState();

        if (fluidState.isIn(FluidTags.WATER)) {
            currentChunk.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.WATER, structureBlockInfoWorld.pos));
        } else if (fluidState.isIn(FluidTags.LAVA)) {
            currentChunk.getFluidTickScheduler().scheduleTick(OrderedTick.create(Fluids.LAVA, structureBlockInfoWorld.pos));
        } else if (fluidState.getFluid() instanceof FlowableFluid) {
            currentChunk.getFluidTickScheduler().scheduleTick(OrderedTick.create(fluidState.getFluid(), structureBlockInfoWorld.pos));
        }
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
