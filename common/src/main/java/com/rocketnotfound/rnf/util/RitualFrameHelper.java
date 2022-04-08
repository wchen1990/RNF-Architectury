package com.rocketnotfound.rnf.util;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.blockentity.RitualFrameBlockEntity;
import com.rocketnotfound.rnf.data.recipes.RNFRecipes;
import com.rocketnotfound.rnf.data.recipes.RuneEngravementRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.CheckForNull;
import java.util.*;

public class RitualFrameHelper {
    // TODO: Figure out why conductorsActorsCache sometimes ends up with empty lists
    private static Map<BlockPos, List<RitualFrameBlockEntity>> conductorActorsCache = new HashMap<>();
    public static void invalidateCache() {
        conductorActorsCache.clear();
    }

    public static void updateAllBlocksFor(RitualFrameBlockEntity start) {
        List<RitualFrameBlockEntity> ordered = getOrderedActors(start.getConductorBE());

        if (start.isConductor()) {
            start.markDirty();
            ordered.stream().forEach((actor) -> {
                actor.updateBlock();
            });
        } else {
            int idx = ordered.indexOf(start);
            int size = ordered.size();
            ordered.subList(idx, size).stream().forEach((actor) -> {
                actor.updateBlock();
            });
        }
    }

    public static void clearInventoryStartingFrom(RitualFrameBlockEntity start) {
        List<RitualFrameBlockEntity> ordered = getOrderedActors(start.getConductorBE());

        if (start.isConductor()) {
            start.clearItem();
            start.updateBlock();
            ordered.stream().forEach((actor) -> {
                actor.clearItem();
                actor.updateBlock();
            });
        } else {
            int idx = ordered.indexOf(start);
            int size = ordered.size();
            ordered.subList(idx, size).stream().forEach((actor) -> {
                actor.clearItem();
                actor.updateBlock();
            });
        }
    }

    public static int getCraftingTicksFor(RitualFrameBlockEntity start) {
        List<RitualFrameBlockEntity> ordered = getOrderedActors(start.getConductorBE());

        int listSize = 0;
        if (start.isConductor()) {
            listSize = ordered.size() + (isLoop(start) ? 0 : 1);
        } else {
            int idx = ordered.indexOf(start);
            int size = ordered.size();
            listSize = ordered.subList(idx, size).size();
        }

        return listSize * RNF.serverConfig().RITUAL.CRAFTING_TICKS_PER_FRAME;
    }

    public static Pair<Optional<Recipe>, Inventory> checkForRecipe(RitualFrameBlockEntity blockEntity, ServerWorld serverWorld) {
        Inventory inv = getCombinedInventoryFrom(blockEntity);
        Optional<Recipe> rec = Optional.empty();

        final BlockPos checkPos;
        BlockState blockState = serverWorld.getBlockState(blockEntity.getPos());
        if (blockState.isOf(RNFBlocks.RITUAL_FRAME.get())) {
            checkPos = blockEntity.getPos().offset(blockState.get(Properties.FACING).getOpposite());
        } else {
            checkPos = blockEntity.getPos();
        }
        final BlockState checkState = serverWorld.getBlockState(checkPos);

        // Hardcode Rune Engraving check
        List<RuneEngravementRecipe> runeEngravements = serverWorld.getRecipeManager().getAllMatches(RNFRecipes.RUNE_ENGRAVEMENT_TYPE.get(), inv, serverWorld);
        if (!checkState.isOf(RNFBlocks.RITUAL_FRAME.get()) && runeEngravements.size() > 0 && runeEngravements.stream().anyMatch((rune) -> checkState.isOf(rune.getBase()))) {
            rec = Optional.of(runeEngravements.stream().filter((rune) -> checkState.isOf(rune.getBase())).findFirst().get());
        } else if (isLoop(blockEntity)) {
            rec = serverWorld.getRecipeManager().getFirstMatch(RNFRecipes.CIRCLE_RITUAL_TYPE.get(), inv, serverWorld);
        } else {
            rec = serverWorld.getRecipeManager().getFirstMatch(RNFRecipes.RITUAL_TYPE.get(), inv, serverWorld);
        }
        return new Pair<>(rec, inv);
    }

    public static boolean isLoop(RitualFrameBlockEntity start) {
        RitualFrameBlockEntity conductor = start.getConductorBE();
        RitualFrameBlockEntity target = (conductor != null) ? conductor.getTargetBE() : null;
        return target != null && target.getConductorBE() == conductor;
    }

    public static Inventory getCombinedInventoryFrom(RitualFrameBlockEntity start) {
        List<RitualFrameBlockEntity> ordered = getOrderedActors(start.getConductorBE());
        DefaultedList<ItemStack> inventory;

        if (start.isConductor()) {
            inventory = DefaultedList.ofSize(ordered.size() + (isLoop(start) ? 0 : 1));

            inventory.add(start.getItemStack());
            ordered.stream().forEach((frame) -> {
                if (!inventory.contains(frame.getItemStack())) {
                    inventory.add(frame.getItemStack());
                }
            });
        } else {
            int idx = ordered.indexOf(start);
            int size = ordered.size();

            List<RitualFrameBlockEntity> sublist = ordered.subList(idx, size);
            inventory = DefaultedList.ofSize(sublist.size());

            inventory.add(start.getItemStack());
            sublist.stream().forEach((frame) -> {
                if (!inventory.contains(frame.getItemStack())) {
                    inventory.add(frame.getItemStack());
                }
            });
        }

        return RitualInventoryHelper.of(inventory);
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

    /**
     * <p>{@code RitualFrameHelper.add} is {@code @Deprecated} since
     * during world load, {@code RitualFrameBlockEntity}'s are loaded fairly
     * arbitrarily, which would result in a disordering of the cache.</p>
     *
     * <p>This should only be used when we're guaranteed that the order in the
     * cache is correct.</p>
     */
    @Deprecated
    public static void add(RitualFrameBlockEntity add) {
        if (add == null || !(add.getWorld() instanceof ServerWorld)) return;

        // Something changed, inform the conductor
        RitualFrameBlockEntity conductor = add.getConductorBE();
        if (conductor == null) return;

        conductor.setPhase(RitualFrameBlockEntity.Phase.DORMANT);

        if (!add.isConductor()) {
            BlockPos conductorPos = add.getConductor();
            List<RitualFrameBlockEntity> list = conductorActorsCache.get(conductorPos);
            if (list == null) {
                list = new ArrayList<>();
                list.add(add);
                conductorActorsCache.put(conductorPos, list);
            } else {
                if (!list.contains(add)) {
                    list.add(add);
                }
            }
        }
    }

    public static void remove(RitualFrameBlockEntity remove) {
        if (remove == null || !(remove.getWorld() instanceof ServerWorld)) return;

        // Something changed, inform the conductor
        RitualFrameBlockEntity conductor = remove.getConductorBE();
        if (conductor == null) return;

        conductor.setPhase(RitualFrameBlockEntity.Phase.DORMANT);

        // Remove removed from the conductor cache
        List<RitualFrameBlockEntity> ordered = getOrderedActors(conductor);
        if (ordered == null) return;

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

        // Something changed, inform the conductor
        RitualFrameBlockEntity conductor = make.getConductorBE();
        if (conductor == null) return;

        conductor.setPhase(RitualFrameBlockEntity.Phase.DORMANT);

        if (!make.isConductor()) {
            List<RitualFrameBlockEntity> ordered = getOrderedActors(conductor);
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

        // We can access these directly rather than using getOrderedActors since we made them conductors
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