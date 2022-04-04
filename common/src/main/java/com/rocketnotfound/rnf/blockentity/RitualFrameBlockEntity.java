package com.rocketnotfound.rnf.blockentity;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.sound.RNFSounds;
import com.rocketnotfound.rnf.util.RitualFrameConnectionHandler;
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
import net.minecraft.sound.SoundEvents;
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

import java.util.Objects;
import java.util.Optional;

public class RitualFrameBlockEntity extends BaseBlockEntity implements IAnimatable, IMultiBlockEntityContainer {
    public enum Phase {
        DORMANT,
        RECIPE_FOUND,
        CRAFTING,
        CRAFTING_DONE
    }

    protected DefaultedList<ItemStack> inventory;
    protected BlockPos conductor;
    protected BlockPos target;
    protected BlockPos targettedBy;
    protected BlockPos lastKnownPos;
    protected Phase phase = Phase.DORMANT;
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

        if (blockEntity.isConductor()) {
            // Play ritual interrupt sound
            if ((blockEntity.prevPhase == Phase.CRAFTING || blockEntity.prevPhase == Phase.RECIPE_FOUND) && blockEntity.isDormant()) {
                serverWorld.playSound(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_INTERRUPT.get(), SoundCategory.BLOCKS, 1F, 1F);
            }

            // Keep track of phase changes
            if (blockEntity.prevPhase == null || blockEntity.prevPhase != blockEntity.getPhase()) {
                blockEntity.prevPhase = blockEntity.getPhase();
            }

            // Phase changes
            if (blockEntity.isDormant()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().CHECK_RECIPE_INTERVAL_TICKS) {
                    blockEntity.phaseTicks = 0;

                    // Lets not waste resources checking for recipes if conductor doesn't have an item
                    if (blockEntity.getItem() != ItemStack.EMPTY) {
                        Pair<Optional<Recipe>, Inventory> pair = RitualFrameConnectionHandler.checkForRecipe(blockEntity, serverWorld);
                        pair.getLeft().ifPresent((ritualRecipe) -> {
                            blockEntity.setPhase(Phase.RECIPE_FOUND);
                        });
                    }
                }
            } else if (blockEntity.isRecipeFound()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().RECIPE_CRAFTING_DELAY_TICKS) {
                    blockEntity.setPhase(Phase.CRAFTING);
                } else {
                    if (blockEntity.phaseTicks % 15 == 0) {
                        float volume = 0.3F * (blockEntity.phaseTicks / RNF.serverConfig().RECIPE_CRAFTING_DELAY_TICKS) + 0.5F;
                        serverWorld.playSound(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_PROGRESS.get(), SoundCategory.BLOCKS, volume, 1F);
                        serverWorld.spawnParticles(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 3, 0, 0, 0, 0.1);
                    }
                }
            } else if (blockEntity.isCrafting()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RitualFrameConnectionHandler.getCraftingTicksFor(blockEntity)) {
                    Pair<Optional<Recipe>, Inventory> pair = RitualFrameConnectionHandler.checkForRecipe(blockEntity, serverWorld);
                    pair.getLeft().ifPresent((ritualRecipe) -> {
                        RitualFrameConnectionHandler.clearInventoryStartingFrom(blockEntity);
                        serverWorld.playSound(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_COMPLETE.get(), SoundCategory.BLOCKS, 1F, 1F);
                        serverWorld.spawnParticles(ParticleTypes.FLASH, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0, 0, 0, 0);
                        serverWorld.spawnParticles(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 50, 0, 0, 0, 0.1);
                        ItemScatterer.spawn(serverWorld, blockPos, DefaultedList.ofSize(1, ritualRecipe.getOutput()));
                    });

                    blockEntity.setPhase(Phase.CRAFTING_DONE);
                } else {
                    if (blockEntity.phaseTicks % 15 == 0) {
                        serverWorld.playSound(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, RNFSounds.RITUAL_GENERIC_PROGRESS.get(), SoundCategory.BLOCKS, 0.8F, 1F);
                        serverWorld.spawnParticles(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 3, 0, 0, 0, 0.1);
                    }
                }
            } else if (blockEntity.isCraftingDone()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().CRAFTING_COOLDOWN) {
                    blockEntity.setPhase(Phase.DORMANT);
                }
            }
        }

        // Spawn particles
        if (!blockEntity.isCraftingDone()) {
            spawnParticles(serverWorld, blockPos, blockState, blockEntity);
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

                if (blockEntity.isRecipeFound() || blockEntity.isCrafting()) {
                    // Spiral path follows straight normal path
                    serverWorld.spawnParticles(recipeParticle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, diff.getX() * diffMul, diff.getY() * diffMul, diff.getZ() * diffMul, speed);
                } else {
                    // Emits from center point of target
                    serverWorld.spawnParticles(particle, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 0, 0, 0, 0, speed);

                    // Emits from center
                    if (blockEntity.getItem() == ItemStack.EMPTY) {
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

        if (blockEntity.isCrafting()) {
            // Emits from center
            serverWorld.spawnParticles(recipeParticle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0, 0, 0, speed);
        } else if (blockEntity.getItem() != ItemStack.EMPTY) {
            // Emits from center
            serverWorld.spawnParticles(particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, count, 0, 0, 0, speed);
        }
    }

    // Getter/Setters
    public DefaultedList<ItemStack> getInventory() { return inventory; }
    public ItemStack getItem() { return inventory.get(0); }
    public void setItem(ItemStack itemStack) {
        getConductorBE().setPhase(Phase.DORMANT);
        inventory.set(0, itemStack);
    }
    public void clearItem() {
        inventory.set(0, ItemStack.EMPTY);
    }

    public boolean isDormant() { return getPhase().equals(Phase.DORMANT); }
    public boolean isRecipeFound() { return getPhase().equals(Phase.RECIPE_FOUND); }
    public boolean isCrafting() { return getPhase().equals(Phase.CRAFTING); }
    public boolean isCraftingDone() { return getPhase().equals(Phase.CRAFTING_DONE); }

    public Phase getPhase() { return (isConductor()) ? phase : getConductorBE().getPhase(); }
    public void setPhase(Phase phase) {
        this.phase = phase;
        this.phaseTicks = 0;
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
        RitualFrameConnectionHandler.remove(this);
        lastKnownPos = pos;
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
    public BlockPos getConductor() {
        return isConductor() ? pos : conductor;
    }

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

    public BlockPos getTarget() {
        return target;
    }

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
        if (nbtCompound.contains("Phase"))
            phase = Phase.valueOf(nbtCompound.getString("Phase"));
        if (nbtCompound.contains("PhaseTicks"))
            phaseTicks = nbtCompound.getInt("PhaseTicks");

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
        if (target != null)
            nbtCompound.put("Target", NbtHelper.fromBlockPos(target));
        if (targettedBy != null)
            nbtCompound.put("TargettedBy", NbtHelper.fromBlockPos(targettedBy));
        if (phase != null && isConductor())
            nbtCompound.putString("Phase", phase.toString());
        if (phaseTicks >= 0 && isConductor())
            nbtCompound.putInt("PhaseTicks", phaseTicks);

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
