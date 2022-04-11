package com.rocketnotfound.rnf.blockentity;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.data.rituals.Ritual;
import com.rocketnotfound.rnf.data.rituals.IAlterAnchorRitual;
import com.rocketnotfound.rnf.data.rituals.IAlterBaseRitual;
import com.rocketnotfound.rnf.data.rituals.IRitual;
import com.rocketnotfound.rnf.item.RNFItems;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.sound.RNFSounds;
import com.rocketnotfound.rnf.util.RitualFrameHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.CheckForNull;
import java.util.*;

public class RitualFrameBlockEntity extends BaseBlockEntity implements IAnimatable, IMultiBlockEntityContainer {
    public enum Phase {
        DORMANT,
        RITUAL_FOUND,
        PERFORMING,
        PERFORMANCE_FINISHED,
        INFUSING
    }

    protected DefaultedList<ItemStack> inventory;
    protected BlockPos conductor;
    protected BlockPos target;
    protected BlockPos targettedBy;
    protected BlockPos lastKnownPos;
    protected BlockPos miscPos;
    protected Phase phase = Phase.DORMANT;
    protected Ritual ritual = Ritual.UNKNOWN;
    protected boolean ritualHasOutput = false;
    protected int phaseTicks = 0;

    protected Phase prevPhase;

    protected boolean updateConnectivity;
    protected boolean firstRun = true;

    private final AnimationFactory factory = new AnimationFactory(this);

    public RitualFrameBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RNFBlockEntities.RITUAL_FRAME.get(), blockPos, blockState);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, RitualFrameBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        // Update last known position if necessary
        BlockPos lastKnownPos = blockEntity.getLastKnownPos();
        BlockPos pos = blockEntity.getPos();
        if (lastKnownPos == null) {
            blockEntity.setLastKnownPos(pos);
        } else if (!lastKnownPos.equals(pos) && pos != null) {
            blockEntity.onPositionChanged();
            return;
        }

        // Update connectivity if necessary
        if (blockEntity.getUpdateConnectivity()) {
            blockEntity.updateConnectivity();
        }

        ItemStack itemStack = blockEntity.getItemStack();
        if (blockEntity.isIndependent() && itemStack.isOf(RNFItems.LUNA.get())) {
            // Ritual infusion
            performRitualInfusion(serverWorld, blockPos, blockState, blockEntity);
        } else {
            // Ritual crafting
            performPhasedRituals(serverWorld, blockPos, blockState, blockEntity);
        }

        // Spawn particles
        if (!blockEntity.isPerformanceDone()) {
            spawnParticles(serverWorld, blockPos, blockState, blockEntity);
        }
    }

    protected static void performRitualInfusion(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualFrameBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getPos();
        ItemStack itemStack = blockEntity.getItemStack();

        if (blockEntity.isInfusing()) {
            blockEntity.phaseTicks++;
            if (blockEntity.miscPos == null) {
                if (blockEntity.phaseTicks > RNF.serverConfig().INFUSE.CHECK_INFUSING_TARGET_INTERVAL_TICKS) {
                    int radius = RNF.serverConfig().INFUSE.INFUSING_RADIUS;
                    int negRagius = radius * -1;
                    BlockPos infuseTarget;

                    Iterator iter = BlockPos.iterate(pos.add(negRagius, negRagius, negRagius), pos.add(radius, radius, radius)).iterator();
                    do {
                        if (!iter.hasNext()) {
                            infuseTarget = null;
                            break;
                        }

                        infuseTarget = (BlockPos) iter.next();
                    } while (!serverWorld.getBlockState(infuseTarget).isOf(RNFBlocks.DRAINED_RUNE_BLOCK.get()));

                    if (infuseTarget != null && serverWorld.getBlockState(infuseTarget).isOf(RNFBlocks.DRAINED_RUNE_BLOCK.get())) {
                        blockEntity.miscPos = infuseTarget;
                    }

                    blockEntity.phaseTicks = 0;
                }
            } else if (serverWorld.getBlockState(blockEntity.miscPos).isOf(RNFBlocks.DRAINED_RUNE_BLOCK.get())) {
                if (blockEntity.phaseTicks > RNF.serverConfig().INFUSE.INFUSING_COMPLETION_TICKS) {
                    // Replace Drained with Rune Block
                    serverWorld.setBlockState(blockEntity.miscPos, RNFBlocks.RUNE_BLOCK.get().getDefaultState());

                    // FX
                    serverWorld.playSound(null, blockEntity.miscPos.getX() + 0.5, blockEntity.miscPos.getY() + 0.5, blockEntity.miscPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_COMPLETE.get(), SoundCategory.BLOCKS, 1F, 1F);
                    serverWorld.spawnParticles(ParticleTypes.FLASH, blockEntity.miscPos.getX() + 0.5, blockEntity.miscPos.getY() + 0.5, blockEntity.miscPos.getZ() + 0.5, 0, 0, 0, 0, 0);
                    serverWorld.spawnParticles(ParticleTypes.END_ROD, blockEntity.miscPos.getX() + 0.5, blockEntity.miscPos.getY() + 0.5, blockEntity.miscPos.getZ() + 0.5, 50, 0, 0, 0, 0.1);

                    // Damage the Luna
                    if (!itemStack.hasNbt() || !itemStack.getNbt().contains("Damage")) {
                        itemStack.setDamage(RNF.serverConfig().INFUSE.INFUSE_PER_LUNA - 1);
                    } else {
                        itemStack.setDamage(itemStack.getDamage() - 1);
                    }
                    if (itemStack.getDamage() <= 0) {
                        blockEntity.clearItem();
                        blockEntity.updateBlock();
                    }

                    blockEntity.miscPos = null;
                    blockEntity.phaseTicks = 0;
                } else {
                    if (blockEntity.phaseTicks % 15 == 0) {
                        serverWorld.playSound(null, blockEntity.miscPos.getX() + 0.5, blockEntity.miscPos.getY() + 0.5, blockEntity.miscPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_PROGRESS.get(), SoundCategory.BLOCKS, 0.8F, 1F);
                        serverWorld.spawnParticles(ParticleTypes.END_ROD, blockEntity.miscPos.getX() + 0.5, blockEntity.miscPos.getY() + 0.5, blockEntity.miscPos.getZ() + 0.5, 3, 0, 0, 0, 0.1);
                    }
                }
            } else {
                blockEntity.miscPos = null;
                blockEntity.becomeDormant();
            }
        } else {
            blockEntity.setPhase(Phase.INFUSING);
        }
    }

    protected static void performPhasedRituals(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualFrameBlockEntity blockEntity) {
        if (blockEntity.isConductor()) {
            // Play ritual interrupt sound
            if ((blockEntity.prevPhase == Phase.PERFORMING || blockEntity.prevPhase == Phase.RITUAL_FOUND) && blockEntity.isDormant()) {
                doInterruptFX(serverWorld, blockEntity);
            }

            // Keep track of phase changes
            if (blockEntity.prevPhase == null || blockEntity.prevPhase != blockEntity.getPhase()) {
                blockEntity.prevPhase = blockEntity.getPhase();
            }

            // Phase changes
            if (blockEntity.isDormant()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().RITUAL.CHECK_RECIPE_INTERVAL_TICKS) {
                    blockEntity.phaseTicks = 0;

                    // Lets not waste resources checking for recipes if conductor doesn't have an item
                    if (blockEntity.getItemStack() != ItemStack.EMPTY) {
                        Pair<Optional<Recipe>, Inventory> pair = RitualFrameHelper.checkForRecipe(blockEntity, serverWorld);
                        pair.getLeft().ifPresent((ritualRecipe) -> {
                            if (ritualRecipe instanceof IRitual) {
                                blockEntity.setRitual(((IRitual) ritualRecipe).getRitualType());
                            }
                            blockEntity.ritualHasOutput = (ritualRecipe.getOutput() != null && !ritualRecipe.getOutput().isEmpty());
                            blockEntity.setPhase(Phase.RITUAL_FOUND);
                        });
                    }
                }
            } else if (blockEntity.isRitualFound()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().RITUAL.RECIPE_CRAFTING_DELAY_TICKS) {
                    blockEntity.setPhase(Phase.PERFORMING);
                } else {
                    if (blockEntity.phaseTicks % 15 == 0) {
                        doProgressFX(serverWorld, blockEntity, true);
                    }
                }
            } else if (blockEntity.isPerforming()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RitualFrameHelper.getCraftingTicksFor(blockEntity)) {
                    Pair<Optional<Recipe>, Inventory> pair = RitualFrameHelper.checkForRecipe(blockEntity, serverWorld);
                    pair.getLeft().ifPresent((ritualRecipe) -> {
                        RitualFrameHelper.clearInventoryStartingFrom(blockEntity);

                        doCompletionFX(serverWorld, blockEntity);

                        if (ritualRecipe instanceof IAlterBaseRitual) {
                            BlockPos baseBlockPos = RitualFrameHelper.getSupportingBlockPos(blockEntity);
                            Block newBlock = ((IAlterBaseRitual) ritualRecipe).alterBase(pair.getRight());
                            if (!serverWorld.getBlockState(baseBlockPos).isOf(newBlock)) {
                                serverWorld.setBlockState(baseBlockPos, newBlock.getDefaultState());
                            }
                        }

                        if (ritualRecipe instanceof IAlterAnchorRitual) {
                            BlockPos anchorBlockPos = RitualFrameHelper.getSupportingBlockPos(RitualFrameHelper.getLastActor(blockEntity));
                            Block newBlock = ((IAlterAnchorRitual) ritualRecipe).alterAnchor(pair.getRight());
                            if (!serverWorld.getBlockState(anchorBlockPos).isOf(newBlock)) {
                                serverWorld.setBlockState(anchorBlockPos, newBlock.getDefaultState());
                            }
                        }

                        ItemScatterer.spawn(serverWorld, blockPos, DefaultedList.ofSize(1, ritualRecipe.craft(pair.getRight())));
                    });

                    blockEntity.setPhase(Phase.PERFORMANCE_FINISHED);
                } else {
                    if (blockEntity.phaseTicks % 15 == 0) {
                        doProgressFX(serverWorld, blockEntity, false);
                    }
                }
            } else if (blockEntity.isPerformanceDone()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().RITUAL.CRAFTING_COOLDOWN) {
                    blockEntity.becomeDormant();
                }
            } else {
                blockEntity.becomeDormant();
            }
        }
    }

    protected static List<BlockPos> getBlockPositionsForFX(RitualFrameBlockEntity blockEntity) {
        List<BlockPos> positions = new ArrayList<>();

        Ritual ritual = blockEntity.getRitual();
        if (ritual == Ritual.ENGRAVING || ritual == Ritual.TETHER) {
            positions.add(RitualFrameHelper.getSupportingBlockPos(blockEntity));
        }
        if (ritual == Ritual.ANCHOR || ritual == Ritual.TETHER) {
            positions.add(RitualFrameHelper.getSupportingBlockPos(RitualFrameHelper.getLastActor(blockEntity)));
        }
        if (blockEntity.ritualHasOutput) {
            positions.add(blockEntity.getPos());
        }

        return positions;
    }

    protected static void doProgressFX(ServerWorld serverWorld, RitualFrameBlockEntity blockEntity, boolean fadeInVolume) {
        float volume = (fadeInVolume) ? 0.3F * (blockEntity.phaseTicks / RNF.serverConfig().RITUAL.RECIPE_CRAFTING_DELAY_TICKS) + 0.5F : 0.8F;
        for (BlockPos blockPos : getBlockPositionsForFX(blockEntity)) {
            serverWorld.playSound(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_PROGRESS.get(), SoundCategory.BLOCKS, volume, 1F);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 3, 0, 0, 0, 0.1);
        }
    }

    protected static void doCompletionFX(ServerWorld serverWorld, RitualFrameBlockEntity blockEntity) {
        for (BlockPos blockPos : getBlockPositionsForFX(blockEntity)) {
            serverWorld.playSound(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_COMPLETE.get(), SoundCategory.BLOCKS, 1F, 1F);
            serverWorld.spawnParticles(ParticleTypes.FLASH, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0, 0, 0, 0);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 50, 0, 0, 0, 0.1);
        }
    }

    protected static void doInterruptFX(ServerWorld serverWorld, RitualFrameBlockEntity blockEntity) {
        for (BlockPos blockPos : getBlockPositionsForFX(blockEntity)) {
            serverWorld.playSound(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_INTERRUPT.get(), SoundCategory.BLOCKS, 1F, 1F);
        }
    }

    protected static void spawnParticles(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualFrameBlockEntity blockEntity) {
        if (blockEntity == null) return;

        final int count = 1;
        final float speed = 0.25f;

        BlockPos target = blockEntity.getTarget();
        ParticleEffect particle = (blockEntity.isConductor()) ? RNFParticleTypes.ENCHANT_NG.get() : RNFParticleTypes.ENCHANT_NG_REV.get();
        ParticleEffect recipeParticle = (blockEntity.isConductor()) ? RNFParticleTypes.END_ROD.get() : RNFParticleTypes.END_ROD_REV.get();

        if (target != null) {
            if (serverWorld.getBlockEntity(target) instanceof RitualFrameBlockEntity) {
                BlockPos diff = target.mutableCopy().subtract(blockPos).add(0.5, 0.5, 0.5);
                final float diffMul = (1 / speed);

                if (blockEntity.isRitualFound() || blockEntity.isPerforming()) {
                    // Spiral path follows straight normal path
                    serverWorld.spawnParticles(RNFParticleTypes.END_ROD_REV.get(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, diff.getX() * diffMul, diff.getY() * diffMul, diff.getZ() * diffMul, speed);
                } else {
                    // Emits from center point of target
                    serverWorld.spawnParticles(particle, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 0, 0, 0, 0, speed);

                    // Emits from center
                    if (blockEntity.getItemStack() == ItemStack.EMPTY) {
                        serverWorld.spawnParticles(particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0, 0, 0, speed);
                    }
                }

                // Straight normal path
                serverWorld.spawnParticles(RNFParticleTypes.ENCHANT_NG_REV.get(), blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, diff.getX() * diffMul, diff.getY() * diffMul, diff.getZ() * diffMul, speed);
            } else {
                blockEntity.setTarget(null);
                blockEntity.markDirty();
            }
        }

        if (blockEntity.isPerforming()) {
            // Emits from center
            serverWorld.spawnParticles(recipeParticle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0, 0, 0, speed);
        } else if (blockEntity.getItemStack() != ItemStack.EMPTY) {
            // Emits from center
            serverWorld.spawnParticles(blockEntity.isInfusing() ? recipeParticle : particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, count, 0, 0, 0, speed);
        }
    }

    // Getter/Setters
    public DefaultedList<ItemStack> getInventory() { return inventory; }
    public ItemStack getItemStack() { return inventory.get(0); }
    public void setItem(ItemStack itemStack) {
        RitualFrameBlockEntity conductor = getConductorBE();
        if (conductor != null) {
            conductor.becomeDormant();
        }
        inventory.set(0, itemStack);
    }
    public void clearItem() {
        inventory.set(0, ItemStack.EMPTY);
    }

    public boolean isDormant() { return getPhase() == Phase.DORMANT; }
    public boolean isRitualFound() { return getPhase() == Phase.RITUAL_FOUND; }
    public boolean isPerforming() { return getPhase() == Phase.PERFORMING; }
    public boolean isPerformanceDone() { return getPhase() == Phase.PERFORMANCE_FINISHED; }
    public boolean isInfusing() { return getPhase() == Phase.INFUSING; }

    public Phase getPhase() {
        if (isConductor()) {
            return phase;
        } else {
            RitualFrameBlockEntity conductor = getConductorBE();
            return (conductor != null) ? conductor.getPhase() : null;
        }
    }
    public void setPhase(Phase phase) {
        this.phase = phase;
        this.phaseTicks = 0;
    }

    public void becomeDormant() {
        this.setPhase(Phase.DORMANT);
        this.setRitual(Ritual.UNKNOWN);
        this.setMiscPos(null);
        this.ritualHasOutput = false;
    }

    public Ritual getRitual() {
        if (isConductor()) {
            return ritual;
        } else {
            RitualFrameBlockEntity conductor = getConductorBE();
            return (conductor != null) ? conductor.getRitual() : null;
        }
    }
    public void setRitual(Ritual ritual) {
        this.ritual = ritual;
    }

    public boolean getUpdateConnectivity() {
        return updateConnectivity || firstRun;
    }

    public void updateConnectivity() {
        firstRun = false;
        updateConnectivity = false;
        if (world.isClient())
            return;
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    public void setLastKnownPos(BlockPos pos) {
        lastKnownPos = pos;
    }

    public void onPositionChanged() {
        RitualFrameHelper.remove(this);
        lastKnownPos = pos;
    }

    public BlockPos getMiscPos() {
        return miscPos;
    }

    public void setMiscPos(BlockPos pos) {
        miscPos = pos;
    }

    public boolean isIndependent() {
        return (
            isConductor() &&
            (
                target == null ||
                pos.getX() == target.getX() &&
                pos.getY() == target.getY() &&
                pos.getZ() == target.getZ()
            ) &&
            (
                targettedBy == null ||
                pos.getX() == targettedBy.getX() &&
                pos.getY() == targettedBy.getY() &&
                pos.getZ() == targettedBy.getZ()
            )
        );
    }

    // Conductor methods
    @Override
    public boolean isConductor() {
        return (
            conductor == null || pos.getX() == conductor.getX()
                && pos.getY() == conductor.getY() && pos.getZ() == conductor.getZ()
        );
    }

    public void removeConductor() {
        if (world.isClient())
            return;
        updateConnectivity = true;
        conductor = null;
    }

    @Override
    public void setConductor(BlockPos conductor) {
        if (world.isClient)
            return;
        if (conductor.equals(this.conductor))
            return;
        this.conductor = conductor;
    }

    @Override
    @CheckForNull
    public BlockPos getConductor() {
        return isConductor() ? pos : conductor;
    }

    @CheckForNull
    public RitualFrameBlockEntity getConductorBE() {
        if (isConductor())
            return this;
        BlockEntity tileEntity = world.getBlockEntity(conductor);
        if (tileEntity instanceof RitualFrameBlockEntity)
            return (RitualFrameBlockEntity) tileEntity;
        return null;
    }

    // Target methods
    public void setTarget(BlockPos target) {
        if (world.isClient())
            return;
        if (pos.equals(target))
            return;
        this.target = target;
    }

    @CheckForNull
    public BlockPos getTarget() {
        return target;
    }

    @CheckForNull
    public RitualFrameBlockEntity getTargetBE() {
        if (target != null) {
            BlockEntity tileEntity = world.getBlockEntity(target);
            if (tileEntity instanceof RitualFrameBlockEntity)
                return (RitualFrameBlockEntity) tileEntity;
        }
        return null;
    }

    // TargettedBy Methods
    public void setTargettedBy(BlockPos targettedBy) {
        if (world.isClient())
            return;
        if (pos.equals(targettedBy))
            return;
        this.targettedBy = targettedBy;
    }

    public BlockPos getTargettedBy() {
        return targettedBy;
    }

    public RitualFrameBlockEntity getTargettedByBE() {
        if (targettedBy != null) {
            BlockEntity tileEntity = world.getBlockEntity(targettedBy);
            if (tileEntity instanceof RitualFrameBlockEntity)
                return (RitualFrameBlockEntity) tileEntity;
        }
        return null;
    }

    // NBT
    @Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);

        BlockPos conductorBefore = conductor;

        updateConnectivity = nbtCompound.contains("Uninitialized");
        conductor = null;
        target = null;
        lastKnownPos = null;

        if (nbtCompound.contains("LastKnownPos"))
            lastKnownPos = NbtHelper.toBlockPos(nbtCompound.getCompound("LastKnownPos"));
        if (nbtCompound.contains("Conductor"))
            conductor = NbtHelper.toBlockPos(nbtCompound.getCompound("Conductor"));
        if (nbtCompound.contains("Target"))
            target = NbtHelper.toBlockPos(nbtCompound.getCompound("Target"));
        if (nbtCompound.contains("TargettedBy"))
            targettedBy = NbtHelper.toBlockPos(nbtCompound.getCompound("TargettedBy"));
        if (nbtCompound.contains("MiscPos"))
            miscPos = NbtHelper.toBlockPos(nbtCompound.getCompound("MiscPos"));
        if (nbtCompound.contains("Phase"))
            phase = Phase.valueOf(nbtCompound.getString("Phase"));
        if (nbtCompound.contains("PhaseTicks"))
            phaseTicks = nbtCompound.getInt("PhaseTicks");
        if (nbtCompound.contains("Ritual"))
            ritual = Ritual.valueOf(nbtCompound.getString("Ritual"));
        if (nbtCompound.contains("RitualHasOutput"))
            ritualHasOutput = nbtCompound.getBoolean("RitualHasOutput");

        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbtCompound, this.inventory);

        boolean changeOfConductor = !Objects.equals(conductorBefore, conductor);
        if (hasWorld() && (changeOfConductor)) {
            world.scheduleBlockRerenderIfNeeded(getPos(), Blocks.AIR.getDefaultState(), getCachedState());
        }
    }

    @Override
    public void writeNbt(NbtCompound nbtCompound) {
        super.writeNbt(nbtCompound);
        if (updateConnectivity)
            nbtCompound.putBoolean("Uninitialized", true);
        if (lastKnownPos != null)
            nbtCompound.put("LastKnownPos", NbtHelper.fromBlockPos(lastKnownPos));
        if (conductor != null && !isConductor())
            nbtCompound.put("Conductor", NbtHelper.fromBlockPos(conductor));
        if (target != null && !target.equals(pos))
            nbtCompound.put("Target", NbtHelper.fromBlockPos(target));
        if (targettedBy != null && !targettedBy.equals(pos))
            nbtCompound.put("TargettedBy", NbtHelper.fromBlockPos(targettedBy));
        if (miscPos != null)
            nbtCompound.put("MiscPos", NbtHelper.fromBlockPos(miscPos));
        if (phase != null && isConductor())
            nbtCompound.putString("Phase", phase.toString());
        if (phaseTicks >= 0 && isConductor())
            nbtCompound.putInt("PhaseTicks", phaseTicks);
        if (ritual != null && isConductor())
            nbtCompound.putString("Ritual", ritual.toString());
        if (ritualHasOutput && isConductor())
            nbtCompound.putBoolean("RitualHasOutput", true);

        Inventories.writeNbt(nbtCompound, this.inventory);
    }

    // Geckolib code
    @SuppressWarnings("unchecked")
    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().transitionLengthTicks = 0;
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.ritual_frame.pillar_idle", true));
        return PlayState.CONTINUE;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
