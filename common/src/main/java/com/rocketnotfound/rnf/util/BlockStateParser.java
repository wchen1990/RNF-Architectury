package com.rocketnotfound.rnf.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import javax.annotation.CheckForNull;

public class BlockStateParser {
    @CheckForNull
    public static BlockStateArgument parse(String parseStr) {
        BlockStateArgumentType bsat = BlockStateArgumentType.blockState();
        try {
            return bsat.parse(new StringReader(parseStr));
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    public static boolean setBlockState(ServerWorld world, BlockPos pos, String blockStateStr) {
        BlockStateArgument bsa = parse(blockStateStr);
        if (bsa != null) {
            return bsa.setBlockState(world, pos, 3);
        }
        return false;
    }
}
