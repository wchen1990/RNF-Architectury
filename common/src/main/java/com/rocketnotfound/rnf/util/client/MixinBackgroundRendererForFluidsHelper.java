package com.rocketnotfound.rnf.util.client;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MixinBackgroundRendererForFluidsHelper {
    public static boolean matchesCondition(FluidState fluidState) {
        return !fluidState.isEmpty()
            && !fluidState.getFluid().getRegistryEntry().isIn(FluidTags.WATER)
            && !fluidState.getFluid().getRegistryEntry().isIn(FluidTags.LAVA);
    }

    public static float getDimensionBrightnessAtEyes(Entity entity) {
        float lightLevelAtEyes = entity.world.getBaseLightLevel(new BlockPos(entity.getCameraPosVec(1)), 0);
        return lightLevelAtEyes / 15f;
    }

    public static FluidState getNearbyFluid(Camera camera) {
        Entity entity = camera.getFocusedEntity();
        World world = entity.world;
        FluidState fluidstate = world.getFluidState(camera.getBlockPos());

        Vec3d currentPos = camera.getPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double offsetDistanceCheck = 0.075D;

        for(Direction direction : Direction.values()) {
            double x = currentPos.getX() + direction.getOffsetX() * offsetDistanceCheck;
            double y = currentPos.getY() + direction.getOffsetY() * offsetDistanceCheck;
            double z = currentPos.getZ() + direction.getOffsetZ() * offsetDistanceCheck;
            mutable.set(x, y, z);
            if(!mutable.equals(camera.getBlockPos())) {
                FluidState neighboringFluidstate = world.getFluidState(mutable);
                fluidstate = neighboringFluidstate;
            }
        }

        return fluidstate;
    }
}
