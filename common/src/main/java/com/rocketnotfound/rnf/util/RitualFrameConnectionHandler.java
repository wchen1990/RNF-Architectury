package com.rocketnotfound.rnf.util;

import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class RitualFrameConnectionHandler {
    private static Map<BlockPos, List<RitualFrameBlockEntity>> conductorActorsCache = new HashMap<>();

    public static BlockPos checkTarget(World world, BlockPos target) {
        if (world.isClient) return target;
        BlockEntity be = world.getBlockEntity(target);
        if (be instanceof RitualFrameBlockEntity) {
            RitualFrameBlockEntity targetBE = (RitualFrameBlockEntity) be;
            RitualFrameBlockEntity targettedBy = targetBE.getTargettedByBE();
            if (targettedBy != null) {
                RitualFrameBlockEntity conductor = targettedBy.getConductorBE();
                if (conductor != null) {
                    List<RitualFrameBlockEntity> list = getOrderedActors(conductor);
                    int count = list.size();
                    if (count > 0) {
                        return list.get(count - 1).getPos();
                    }
                }
            }
            return target;
        }
        return null;
    }

    public static List<RitualFrameBlockEntity> getOrderedActors(RitualFrameBlockEntity conductor) {
        List<RitualFrameBlockEntity> list = conductorActorsCache.get(conductor.getPos());
        if (list == null) {
            list = new ArrayList<>();
            RitualFrameBlockEntity targettedBy = conductor.getTargettedByBE();
            while (targettedBy != null) {
                list.add(targettedBy);
                targettedBy = targettedBy.getTargettedByBE();
            }
        }
        return list;
    }

    public static void add(RitualFrameBlockEntity add) {
        if (!add.isConductor()) {
            BlockPos conductor = add.getConductor();
            List<RitualFrameBlockEntity> list = conductorActorsCache.get(conductor);
            if (list == null) {
                list = new ArrayList<>();
                conductorActorsCache.put(conductor, list);
            }
            if (!list.contains(add)) {
                list.add(add);
            }
        }
    }

    public static void remove(RitualFrameBlockEntity remove) {
        RitualFrameBlockEntity target = remove.getTargetBE();
        if (target != null) {
            target.setTargettedBy(null);
            target.updateBlock();
        }

        RitualFrameBlockEntity targettedBy = remove.getTargettedByBE();
        List<RitualFrameBlockEntity> ordered = getOrderedActors(remove.getConductorBE());
        if (targettedBy != null) {
            targettedBy.setTarget(null);
            targettedBy.removeConductor();
            targettedBy.updateBlock();

            if (!remove.isConductor()) {
                int idx = ordered.indexOf(targettedBy);
                int size = ordered.size();
                if (idx + 1 < size) {
                    ordered.subList(idx + 1, size - 1).forEach((actor) -> {
                        actor.setConductor(targettedBy.getPos());
                        actor.updateBlock();
                    });
                }
            } else {
                ordered.forEach((actor) -> {
                    actor.setConductor(targettedBy.getPos());
                    actor.updateBlock();
                });
            }
        }

        // Invalidate cache
        conductorActorsCache.remove(remove.getConductor());
    }
}
