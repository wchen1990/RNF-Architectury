package com.rocketnotfound.rnf.blockentity;

import com.rocketnotfound.rnf.client.particle.RNFParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.DefaultParticleType;
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

public class RitualFrameBlockEntity extends BaseBlockEntity implements IAnimatable, IMultiBlockEntityContainer {
    protected DefaultedList<ItemStack> inventory;
    protected BlockPos conductor;
    protected BlockPos target;
    protected BlockPos lastKnownPos;
    protected boolean updateConnectivity;

    private final AnimationFactory factory = new AnimationFactory(this);

    public RitualFrameBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RNFBlockEntities.RITUAL_FRAME.get(), blockPos, blockState);
        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, RitualFrameBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld)) return;

        ServerWorld serverWorld = ((ServerWorld) world);

        BlockPos lastKnownPos = blockEntity.getLastKnownPos();
        BlockPos pos = blockEntity.getPos();
        if (lastKnownPos == null) {
            blockEntity.setLastKnownPos(pos);
        } else if (!lastKnownPos.equals(pos) && pos != null) {
            blockEntity.onPositionChanged();
            return;
        }

        if (blockEntity.getUpdateConnectivity()) {
            blockEntity.updateConnectivity();
        }

        if (blockEntity.getItem() != ItemStack.EMPTY) {
            int count = 1;
            float speed = 0.25f;
            DefaultParticleType particle = (blockEntity.isConductor()) ? RNFParticleTypes.ENCHANT_NG.get() : RNFParticleTypes.ENCHANT_NG_REV.get();
            serverWorld.spawnParticles(particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, count, 0, 0, 0, speed);

            BlockPos target = blockEntity.getTarget();
            if (target != null) {
                BlockPos diff = target.mutableCopy().subtract(blockPos).add(0.5, 0.5, 0.5);
                float diffMul = (1 / speed);
                serverWorld.spawnParticles(particle, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 0, 0, 0, 0, speed);
                serverWorld.spawnParticles(particle, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 0, diff.getX() * diffMul, diff.getY() * diffMul, diff.getZ() * diffMul, speed);
            }
        }
    }

    // Getter/Setters
    public DefaultedList<ItemStack> getInventory() { return inventory; }
    public ItemStack getItem() { return inventory.get(0); }
    public void setItem(ItemStack itemStack) { inventory.set(0, itemStack); }

    public boolean getUpdateConnectivity() {
        return updateConnectivity;
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (world.isClient())
            return;
        if (!isConductor())
            return;
        //ItemVaultConnectivityHandler.formVaults(this);
    }

    @Override
    public BlockPos getLastKnownPos() {
        return lastKnownPos;
    }

    public void setLastKnownPos(BlockPos pos) {
        lastKnownPos = pos;
    }

    @Override
    public boolean isConductor() {
        return conductor == null || pos.getX() == conductor.getX()
                && pos.getY() == conductor.getY() && pos.getZ() == conductor.getZ();
    }

    public void onPositionChanged() {
        removeConductor(true);
        lastKnownPos = pos;
    }

    public RitualFrameBlockEntity getConductorBE() {
        if (isConductor())
            return this;
        BlockEntity tileEntity = world.getBlockEntity(conductor);
        if (tileEntity instanceof RitualFrameBlockEntity)
            return (RitualFrameBlockEntity) tileEntity;
        return null;
    }

    public void removeConductor(boolean keepContents) {
        if (world.isClient())
            return;
        updateConnectivity = true;
        conductor = null;
        updateBlock();
    }

    @Override
    public void setConductor(BlockPos conductor) {
        if (world.isClient)
            return;
        if (conductor.equals(this.conductor))
            return;
        this.conductor = conductor;
        updateBlock();
    }

    @Override
    public BlockPos getConductor() {
        return isConductor() ? pos : conductor;
    }

    public void setTarget(BlockPos target) {
        this.target = target;
    }

    public BlockPos getTarget() {
        return target;
    }

    // NBT
    @Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);

        BlockPos conductorBefore = conductor;
        /*
        int prevSize = radius;
        int prevLength = length;
         */

        updateConnectivity = nbtCompound.contains("Uninitialized");
        conductor = null;
        lastKnownPos = null;

        if (nbtCompound.contains("LastKnownPos"))
            lastKnownPos = NbtHelper.toBlockPos(nbtCompound.getCompound("LastKnownPos"));
        if (nbtCompound.contains("Conductor"))
            conductor = NbtHelper.toBlockPos(nbtCompound.getCompound("Conductor"));

        if (isConductor()) {
            /*
            radius = nbtCompound.getInt("Size");
            length = nbtCompound.getInt("Length");
             */
        }

        this.inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(nbtCompound, this.inventory);

        boolean changeOfConductor =
                conductorBefore == null ? conductor != null : !conductorBefore.equals(conductor);
        if (hasWorld() && (changeOfConductor
                //|| prevSize != radius || prevLength != length
        )) {
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
        if (!isConductor())
            nbtCompound.put("Conductor", NbtHelper.fromBlockPos(conductor));
        if (isConductor()) {
            /*
            nbtCompound.putInt("Size", radius);
            nbtCompound.putInt("Length", length);
            */
        }

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
