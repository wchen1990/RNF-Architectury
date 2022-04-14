package com.rocketnotfound.rnf.blockentity;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.data.managers.SpellManager;
import com.rocketnotfound.rnf.data.spells.ISpell;
import com.rocketnotfound.rnf.data.spells.RNFSpells;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RitualTranscriberBlockEntity extends BaseBlockEntity {
    public enum Phase {
        DORMANT,
        ACTIVE,
        TRANSCRIBING,
        RESTING
    }

    protected Phase phase = Phase.DORMANT;
    protected Phase prevPhase;
    protected int phaseTicks = 0;

    protected int castTime = 0;

    protected boolean hasOutput = false;

    public RitualTranscriberBlockEntity(BlockPos pos, BlockState state) {
        super(RNFBlockEntities.RITUAL_TRANSCRIBER.get(), pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        if (blockEntity.prevPhase == null || blockEntity.prevPhase != blockEntity.getPhase()) {
            blockEntity.prevPhase = blockEntity.getPhase();
        }

        Direction facing = blockState.get(Properties.FACING);
        Boolean powered = blockState.get(Properties.POWERED);

        if (powered) {
            int search = RNF.serverConfig().TRANSCRIBE.SEARCH_LIMIT;
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();
            double xOff = search * (facing.getOffsetX());
            double yOff = search * (facing.getOffsetY());
            double zOff = search * (facing.getOffsetZ());
            Box box = new Box(x, y, z, x + xOff, y + yOff, z + zOff);

            if (blockEntity.isDormant()) {
                blockEntity.setPhase(Phase.ACTIVE);
            } else if (blockEntity.isActive()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().TRANSCRIBE.CHECK_REQUIREMENTS_INTERVAL_TICKS) {
                    List<BlockPos> positions = BlockPos.stream(box).map((sPos) -> sPos.toImmutable()).collect(Collectors.toList());
                    Optional<ISpell> optSpell = SpellManager.getInstance().getFirstMatch(RNFSpells.SINGLE_SPELL_TYPE.get(), positions, serverWorld);
                    optSpell.ifPresent((spell) -> {
                        blockEntity.castTime = RNF.serverConfig().TRANSCRIBE.ACTION_TICKS_PER_LENGTH * spell.getLength();
                        blockEntity.hasOutput = (spell.getOutput() != null && !spell.getOutput().isEmpty());
                        blockEntity.setPhase(Phase.TRANSCRIBING);
                    });
                }
            } else if (blockEntity.isTranscribing()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > blockEntity.castTime) {
                    List<BlockPos> positions = BlockPos.stream(box).map((sPos) -> sPos.toImmutable()).collect(Collectors.toList());
                    Optional<ISpell> optSpell = SpellManager.getInstance().getFirstMatch(RNFSpells.SINGLE_SPELL_TYPE.get(), positions, serverWorld);
                    optSpell.ifPresent((spell) -> {
                        spell.cast(positions, serverWorld);
                        ItemScatterer.spawn(serverWorld, blockPos.offset(facing.getOpposite()), DefaultedList.ofSize(1, spell.craft(null)));
                        blockEntity.setPhase(Phase.RESTING);
                    });
                }
            } else if (blockEntity.isResting()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().TRANSCRIBE.ACTION_COOLDOWN) {
                    blockEntity.becomeDormant();
                }
            }
        } else if (!powered && !blockEntity.isDormant()) {
            blockEntity.becomeDormant();
        }

        spawnParticles(serverWorld, blockPos, blockState, blockEntity);
    }

    protected static void spawnParticles(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        Boolean powered = blockState.get(Properties.POWERED);
        Direction facing = blockState.get(Properties.FACING).getOpposite();

        final int count = 1;
        final float speed = 0.25f;

        double x = blockPos.getX() + 0.5 - (facing.getOffsetX() * 0.5) + (facing.getOffsetX());
        double y = blockPos.getY() + 0.5 - (facing.getOffsetY() * 0.5) + (facing.getOffsetY());
        double z = blockPos.getZ() + 0.5 - (facing.getOffsetZ() * 0.5) + (facing.getOffsetZ());

        serverWorld.spawnParticles(powered ? RNFParticleTypes.END_ROD.get() : RNFParticleTypes.ENCHANT_NG.get(), x, y, z, count, 0, 0, 0, speed);
    }

    public boolean isDormant() { return getPhase() == Phase.DORMANT; }
    public boolean isActive() { return getPhase() == Phase.ACTIVE; }
    public boolean isTranscribing() { return getPhase() == Phase.TRANSCRIBING; }
    public boolean isResting() { return getPhase() == Phase.RESTING; }

    public Phase getPhase() {
        return this.phase;
    }
    public void setPhase(Phase phase) {
        this.phase = phase;
        this.phaseTicks = 0;
    }
    public void becomeDormant() {
        setPhase(Phase.DORMANT);
        this.castTime = 0;
        this.hasOutput = false;
    }

    // NBT
    @Override
    public void readNbt(NbtCompound nbtCompound) {
        super.readNbt(nbtCompound);

        if (nbtCompound.contains("Phase"))
            phase = Phase.valueOf(nbtCompound.getString("Phase"));
        if (nbtCompound.contains("PhaseTicks"))
            phaseTicks = nbtCompound.getInt("PhaseTicks");
        if (nbtCompound.contains("CastTime"))
            castTime = nbtCompound.getInt("CastTime");
        if (nbtCompound.contains("HasOutput"))
            hasOutput = nbtCompound.getBoolean("HasOutput");
    }

    @Override
    public void writeNbt(NbtCompound nbtCompound) {
        super.writeNbt(nbtCompound);

        if (phase != null)
            nbtCompound.putString("Phase", phase.toString());
        if (phaseTicks >= 0)
            nbtCompound.putInt("PhaseTicks", phaseTicks);
        if (castTime >= 0)
            nbtCompound.putInt("CastTime", castTime);
        if (hasOutput)
            nbtCompound.putBoolean("HasOutput", true);
    }
}
