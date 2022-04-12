package com.rocketnotfound.rnf.blockentity;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class RitualTranscriberBlockEntity extends BaseBlockEntity {
    protected boolean onlyOnce = false;

    public RitualTranscriberBlockEntity(BlockPos pos, BlockState state) {
        super(RNFBlockEntities.RITUAL_TRANSCRIBER.get(), pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        Direction facing = blockState.get(Properties.FACING);
        Boolean powered = blockState.get(Properties.POWERED);

        if (powered && !blockEntity.onlyOnce) {
            blockEntity.onlyOnce = true;
            int search = RNF.serverConfig().TRANSCRIBE.SEARCH_LIMIT;
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();
            double xOff = search * (facing.getOffsetX());
            double yOff = search * (facing.getOffsetY());
            double zOff = search * (facing.getOffsetZ());
            Box box = new Box(x, y, z, x + xOff, y + yOff, z + zOff);
            List<BlockState> list = serverWorld.getStatesInBox(box).collect(Collectors.toList());
        } else if (!powered && blockEntity.onlyOnce) {
            blockEntity.onlyOnce = false;
        }

        spawnParticles(serverWorld, blockPos, blockState, blockEntity);
    }

    protected static void spawnParticles(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        Boolean powered = blockState.get(Properties.POWERED);
        Direction facing = blockState.get(Properties.FACING).getOpposite();

        final int count = 1;
        final float speed = 0.25f;

        double x = blockPos.getX() + 0.5 + (facing.getOffsetX());
        double y = blockPos.getY() + 0.5 + (facing.getOffsetY());
        double z = blockPos.getZ() + 0.5 + (facing.getOffsetZ());

        serverWorld.spawnParticles(powered ? RNFParticleTypes.END_ROD.get() : RNFParticleTypes.ENCHANT_NG.get(), x, y, z, count, 0, 0, 0, speed);
    }
}
