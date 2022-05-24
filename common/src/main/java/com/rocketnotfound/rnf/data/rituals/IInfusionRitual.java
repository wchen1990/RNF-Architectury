package com.rocketnotfound.rnf.data.rituals;

import com.rocketnotfound.rnf.util.BlockStateParser;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public interface IInfusionRitual {
    Pair<String, BlockStateArgument> getTargetPair();
    Pair<String, BlockStateArgument> getResultPair();
    int getNumInfusions();
    int getSearchRadius();

    default boolean testTarget(ServerWorld serverWorld, BlockPos targetPos) {
        Pair<String, BlockStateArgument> targetPair = getTargetPair();
        if (targetPair != null) {
            BlockStateArgument targetBsa = targetPair.getRight();
            return targetBsa != null && targetBsa.test(serverWorld, targetPos);
        }
        return false;
    }

    default boolean tryInfuse(ServerWorld serverWorld, BlockPos targetPos) {
        Pair<String, BlockStateArgument> targetPair = getTargetPair();
        Pair<String, BlockStateArgument> resultPair = getResultPair();

        if (targetPair != null && resultPair != null) {
            BlockStateArgument targetBsa = targetPair.getRight();
            if (targetBsa != null && targetBsa.test(serverWorld, targetPos)) {
                BlockStateArgument resultBsa = resultPair.getRight();
                if (resultBsa != null) {
                    return BlockStateParser.setBlockState(serverWorld, targetPos, resultBsa);
                }
            }
        }

        return false;
    }
}
