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
                if (list.contains(targettedBy)) {
                    break;
                }
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
                list.add(add);
                conductorActorsCache.put(conductor, list);
            } else {
                if (!list.contains(add)) {
                    list.add(add);
                }
            }
        }
    }

    public static void remove(RitualFrameBlockEntity remove) {
        if (remove == null) return;

        // Remove removed from the conductor cache
        List<RitualFrameBlockEntity> ordered = getOrderedActors(remove.getConductorBE());
        ordered.remove(remove);

        // Remove TargettedBy reference from the remove's target
        RitualFrameBlockEntity target = remove.getTargetBE();
        if (target != null) {
            target.setTargettedBy(null);
            target.markDirty();
        }

        // Remove target from the one targetting the removed
        // Also, since the one targetting is no longer targetting anything it becomes a new conductor
        RitualFrameBlockEntity targettedBy = remove.getTargettedByBE();
        if (targettedBy != null) {
            List<RitualFrameBlockEntity> temp = null;

            ordered.remove(targettedBy);

            if (!remove.isConductor()) {
                RitualFrameBlockEntity tgtByTgtByBE = targettedBy.getTargettedByBE();
                if (tgtByTgtByBE != null) {
                    int idx = ordered.indexOf(tgtByTgtByBE);
                    int size = ordered.size();

                    temp = new ArrayList<>(ordered.subList(idx, size));
                    temp.forEach((actor) -> {
                        actor.setConductor(targettedBy.getPos());
                        actor.markDirty();
                    });

                    ordered.removeAll(temp);
                }
            } else {
                ordered.forEach((actor) -> {
                    actor.setConductor(targettedBy.getPos());
                    actor.markDirty();
                });

                temp = new ArrayList<>(ordered);

                ordered.clear();
            }

            // Update caches
            if (ordered.size() == 0) {
                conductorActorsCache.remove(remove.getConductor());
            }
            if (temp != null && temp.size() > 0) {
                conductorActorsCache.put(targettedBy.getPos(), temp);
            }

            // TargettedBy is a conductor now
            targettedBy.setTarget(null);
            targettedBy.removeConductor();
            targettedBy.markDirty();
        }

        // Remove is a conductor now
        remove.setTarget(null);
        remove.setTargettedBy(null);
        remove.removeConductor();
        remove.markDirty();
    }
}
