package com.rocketnotfound.rnf.world.gen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldView;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomlyReplaceBlockProcessor extends StructureProcessor {
    public static final RandomlyReplaceBlockProcessor INSTANCE = new RandomlyReplaceBlockProcessor("", Collections.EMPTY_LIST, false);
    public static final Codec<RandomlyReplaceBlockProcessor> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("input").orElse(null).forGetter((ruleTest) -> ruleTest.input),
            Codec.list(Codec.STRING).fieldOf("outputs").orElse(null).forGetter((ruleTest) -> ruleTest.outputs),
            Codec.BOOL.fieldOf("inherit_properties").orElse(false).forGetter((ruleTest) -> ruleTest.inheritProps)
    ).apply(instance, RandomlyReplaceBlockProcessor::new));
    public static final StructureProcessorType<RandomlyReplaceBlockProcessor> TYPE = () -> CODEC;

    protected final String input;
    protected final List<String> outputs;
    protected final Boolean inheritProps;

    public RandomlyReplaceBlockProcessor(String input, List<String> outputs, Boolean inheritProps) {
        this.input = input;
        this.outputs = outputs;
        this.inheritProps = inheritProps;
    }

    @Override
    public Structure.StructureBlockInfo process(WorldView worldView, BlockPos blockPos, BlockPos blockPos2, Structure.StructureBlockInfo structureBlockInfo, Structure.StructureBlockInfo structureBlockInfo2, StructurePlacementData structurePlacementData) {
        Random random = structurePlacementData.getRandom(structureBlockInfo2.pos);
        if (input != null && outputs != null && outputs.size() > 0) {
            Block inputBlock = Registry.BLOCK.get(new Identifier(input));
            if (structureBlockInfo2.state.getBlock() == inputBlock) {
                int index = random.nextInt(outputs.size());
                String chosenOutput = outputs.get(index);
                Block outputBlock = Registry.BLOCK.get(new Identifier(chosenOutput));

                BlockState blockState = outputBlock.getDefaultState();
                if (inheritProps) {
                    for (Property prop : structureBlockInfo2.state.getProperties()) {
                        blockState = blockState.with(prop, structureBlockInfo2.state.get(prop));
                    }
                }

                return new Structure.StructureBlockInfo(structureBlockInfo2.pos, blockState, null);
            }
        }

        return structureBlockInfo2;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TYPE;
    }
}
