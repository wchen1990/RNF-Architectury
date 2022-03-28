package com.rocketnotfound.rnf.blockentity;

import com.rocketnotfound.rnf.data.recipes.RNFRecipes;
import com.rocketnotfound.rnf.data.recipes.RitualRecipe;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.util.RitualFrameConnectionHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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
    protected DefaultedList<ItemStack> inventory;
    protected BlockPos conductor;
    protected BlockPos target;
    protected BlockPos targettedBy;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;
    protected boolean firstRun = true;

    protected boolean recipeFound = false;

    private final AnimationFactory factory = new AnimationFactory(this);

    public RitualFrameBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RNFBlockEntities.RITUAL_FRAME.get(), blockPos, blockState);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, RitualFrameBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld)) return;

        ServerWorld serverWorld = ((ServerWorld) world);

        // Update last known position if necessary
        BlockPos lastKnownPos = blockEntity.getLastKnownPos();
        BlockPos pos = blockEntity.getPos();
        if (lastKnownPos == null) {
            blockEntity.setLastKnownPos(pos);
        } else if (!lastKnownPos.equals(pos) && pos != null) {
            blockEntity.onPositionChanged();
            return;
        }

        if (blockEntity.firstRun) {
            var test = serverWorld.getRecipeManager().listAllOfType(RNFRecipes.RITUAL_TYPE.get());
            test.size();
        }

        // Update connectivity if necessary
        if (blockEntity.getUpdateConnectivity()) {
            blockEntity.updateConnectivity();
        }

        if (blockEntity.isConductor()) {
            Optional<RitualRecipe> recipe = serverWorld.getRecipeManager().getFirstMatch(RNFRecipes.RITUAL_TYPE.get(), RitualFrameConnectionHandler.getCombinedInventoryFrom(blockEntity), serverWorld);
            recipe.ifPresent((ritualRecipe) -> {
                blockEntity.setRecipeFound(true);
            });
        }

        if (blockEntity.isRecipeFound()) {
            serverWorld.spawnParticles(ParticleTypes.END_ROD, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 1, 0, 0, 0, 0.25);
        }

        // Spawn particles
        spawnParticles(serverWorld, blockPos, blockState, blockEntity);
    }

    protected static void spawnParticles(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualFrameBlockEntity blockEntity) {
        int count = 1;
        float speed = 0.25f;
        BlockPos target = blockEntity.getTarget();
        DefaultParticleType particle = (blockEntity.isConductor()) ? RNFParticleTypes.ENCHANT_NG.get() : RNFParticleTypes.ENCHANT_NG_REV.get();
        if (target != null) {
            if (serverWorld.getBlockEntity(target) instanceof RitualFrameBlockEntity) {
                BlockPos diff = target.mutableCopy().subtract(blockPos).add(0.5, 0.5, 0.5);
                float diffMul = (1 / speed);
                serverWorld.spawnParticles(particle, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 0, 0, 0, 0, speed);
                serverWorld.spawnParticles(particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, diff.getX() * diffMul, diff.getY() * diffMul, diff.getZ() * diffMul, speed);
                if (blockEntity.getItem() == ItemStack.EMPTY) {
                    serverWorld.spawnParticles(particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, 0, 0, 0, speed);
                }
            } else {
                blockEntity.setTarget(null);
                blockEntity.markDirty();
            }
        }
        if (blockEntity.getItem() != ItemStack.EMPTY) {
            serverWorld.spawnParticles(particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, count, 0, 0, 0, speed);
        }
    }

    // Getter/Setters
    public DefaultedList<ItemStack> getInventory() { return inventory; }
    public ItemStack getItem() { return inventory.get(0); }
    public void setItem(ItemStack itemStack) { inventory.set(0, itemStack); }
    public boolean isRecipeFound() { return recipeFound; }
    public void setRecipeFound(boolean found) { recipeFound = found; }

    public boolean getUpdateConnectivity() {
        return updateConnectivity || firstRun;
    }

    public void updateConnectivity() {
        firstRun = false;
        updateConnectivity = false;
        if (world.isClient())
            return;
        RitualFrameConnectionHandler.add(this);
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
            target == null || pos.getX() == target.getX()
                && pos.getY() == target.getY() && pos.getZ() == target.getZ()
        ) && (
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
        if (target != null && !isConductor())
            nbtCompound.put("Target", NbtHelper.fromBlockPos(target));
        if (targettedBy != null)
            nbtCompound.put("TargettedBy", NbtHelper.fromBlockPos(targettedBy));

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
