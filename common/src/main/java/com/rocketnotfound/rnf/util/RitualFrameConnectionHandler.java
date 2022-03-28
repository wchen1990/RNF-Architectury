package com.rocketnotfound.rnf.util;

import com.google.common.collect.Lists;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.CheckForNull;
import java.util.*;

public class RitualFrameConnectionHandler {
    private static Map<BlockPos, List<RitualFrameBlockEntity>> conductorActorsCache = new HashMap<>();
    public static void invalidateCache() {
        conductorActorsCache.clear();
    }

    public static Inventory getCombinedInventoryFrom(RitualFrameBlockEntity start) {
        List<RitualFrameBlockEntity> ordered = getOrderedActors(start.getConductorBE());
        DefaultedList<ItemStack> inventory;

        if (start.isConductor()) {
            inventory = DefaultedList.ofSize(ordered.size() + 1);

            inventory.add(start.getItem());
            ordered.stream().forEach((frame) -> {
                inventory.add(frame.getItem());
            });
        } else {
            int idx = ordered.indexOf(start);
            int size = ordered.size();

            List<RitualFrameBlockEntity> sublist = ordered.subList(idx, size);
            inventory = DefaultedList.ofSize(sublist.size());

            inventory.add(start.getItem());
            sublist.stream().forEach((frame) -> {
                inventory.add(frame.getItem());
            });
        }

        return ReadOnlyInventory.of(inventory);
    }

    @CheckForNull
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

    @CheckForNull
    public static List<RitualFrameBlockEntity> getOrderedActors(RitualFrameBlockEntity conductor) {
        if (conductor == null || !(conductor.getWorld() instanceof ServerWorld)) return null;

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
        if (add == null || !(add.getWorld() instanceof ServerWorld)) return;

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
        if (remove == null || !(remove.getWorld() instanceof ServerWorld)) return;

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

    public static void makeConductor(RitualFrameBlockEntity make) {
        if (make == null || !(make.getWorld() instanceof ServerWorld)) return;

        if (!make.isConductor()) {
            List<RitualFrameBlockEntity> ordered = getOrderedActors(make.getConductorBE());
            List<RitualFrameBlockEntity> temp = null;

            ordered.remove(make);

            RitualFrameBlockEntity targettedBy = make.getTargettedByBE();
            if (targettedBy != null) {
                int idx = ordered.indexOf(targettedBy);
                int size = ordered.size();

                temp = new ArrayList<>(ordered.subList(idx, size));
                temp.forEach((actor) -> {
                    actor.setConductor(make.getPos());
                    actor.markDirty();
                });

                ordered.removeAll(temp);
            }

            // Update caches
            if (ordered.size() == 0) {
                conductorActorsCache.remove(make.getConductor());
            }
            if (temp != null && temp.size() > 0) {
                conductorActorsCache.put(make.getPos(), temp);
            }

            if (make.getTarget() != null) {
                RitualFrameBlockEntity targetBE = make.getTargetBE();
                targetBE.setTargettedBy(null);
                targetBE.markDirty();
            }

            // Make is a conductor now
            make.setTarget(null);
            make.removeConductor();
            make.markDirty();
        }
    }

    public static void target(RitualFrameBlockEntity from, RitualFrameBlockEntity to) {
        if (
            from == null ||
            to == null ||
            !(from.getWorld() instanceof ServerWorld) ||
            !(to.getWorld() instanceof ServerWorld)
        ) {
            return;
        }

        if (!from.isConductor()) makeConductor(from);
        if (to.getTargettedByBE() != null) makeConductor(to.getTargettedByBE());

        List<RitualFrameBlockEntity> orderedFrom = conductorActorsCache.get(from.getPos());
        List<RitualFrameBlockEntity> orderedTo = conductorActorsCache.get(to.getConductor());

        boolean addToCache = false;
        if (orderedTo == null) {
            addToCache = true;
            orderedTo = new ArrayList<>();
        }

        from.setTarget(to.getPos());
        from.setConductor(to.getConductor());
        to.setTargettedBy(from.getPos());

        if (orderedFrom != null) {
            orderedFrom.stream().forEach((actor) -> {
                actor.setConductor(to.getConductor());
                actor.markDirty();
            });
        }

        if (!from.equals(to.getConductor())) {
            orderedTo.add(from);
        }
        if (orderedFrom != null) {
            orderedTo.addAll(orderedFrom);
            orderedFrom.clear();
        }

        conductorActorsCache.remove(from.getPos());
        if (addToCache && orderedTo.size() > 0) {
            conductorActorsCache.put(to.getConductor(), orderedTo);
        }

        from.markDirty();
        to.markDirty();
    }
}
