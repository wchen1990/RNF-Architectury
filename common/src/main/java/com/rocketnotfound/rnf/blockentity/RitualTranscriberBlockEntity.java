package com.rocketnotfound.rnf.blockentity;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.data.managers.SpellManager;
import com.rocketnotfound.rnf.data.spells.ISpell;
import com.rocketnotfound.rnf.data.spells.Spell;
import com.rocketnotfound.rnf.particle.RNFParticleTypes;
import com.rocketnotfound.rnf.sound.RNFSounds;
import com.rocketnotfound.rnf.util.ItemEntityHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RitualTranscriberBlockEntity extends BaseBlockEntity {
    public enum Phase {
        DORMANT,
        ACTIVE,
        TRANSCRIBING,
        PRIMED,
        COMPLETE,
        RESTING
    }

    protected Phase phase = Phase.DORMANT;
    protected Phase prevPhase;
    protected int phaseTicks = 0;

    protected int spellLength = 0;
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
        Direction opposite = facing.getOpposite();
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
                    blockEntity.phaseTicks = 0;

                    List<BlockPos> positions = BlockPos.stream(box).map((sPos) -> sPos.toImmutable()).collect(Collectors.toList());
                    Optional<ISpell> optSpell = SpellManager.getInstance().getFirstMatch(positions, serverWorld);
                    optSpell.ifPresent((spell) -> {
                        blockEntity.spellLength = spell.getLength();
                        blockEntity.castTime = RNF.serverConfig().TRANSCRIBE.ACTION_TICKS_PER_LENGTH * spell.getLength();
                        blockEntity.hasOutput = (spell.getOutput() != null && !spell.getOutput().isEmpty());

                        blockEntity.setPhase(Phase.TRANSCRIBING);
                    });
                }
            } else if (blockEntity.isTranscribing()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > blockEntity.castTime) {
                    List<BlockPos> positions = BlockPos.stream(box).map((sPos) -> sPos.toImmutable()).collect(Collectors.toList());
                    Optional<ISpell> optSpell = SpellManager.getInstance().getFirstMatch(positions, serverWorld);
                    optSpell.ifPresent((spell) -> {
                        blockEntity.setPhase(getPhaseForSpellType(spell.getSpellType()));
                    });

                    if (optSpell.isEmpty()) {
                        blockEntity.setPhase(Phase.RESTING);
                    }
                } else {
                    doTranscribeFX(serverWorld, blockPos, blockState, blockEntity);
                }
            } else if (blockEntity.isPrimed()) {
                LivingEntity entity = serverWorld.getClosestEntity(
                    LivingEntity.class,
                    TargetPredicate.createNonAttackable(),
                    null,
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ(),
                    new Box(blockPos).stretch(Vec3d.of(opposite.getVector().multiply(RNF.serverConfig().TRANSCRIBE.PRIMED_TRIGGER_DISTANCE)))
                );

                if (entity != null) {
                    blockEntity.setPhase(Phase.COMPLETE);
                } else {
                    doPrimedFX(serverWorld, blockPos, blockState, blockEntity);
                }
            } else if (blockEntity.isCompleting()) {
                LivingEntity entity = serverWorld.getClosestEntity(
                    LivingEntity.class,
                    TargetPredicate.createNonAttackable(),
                    null,
                    blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ(),
                    new Box(blockPos).stretch(Vec3d.of(opposite.getVector().multiply(RNF.serverConfig().TRANSCRIBE.PRIMED_TRIGGER_DISTANCE)))
                );

                List<BlockPos> positions = BlockPos.stream(box).map((sPos) -> sPos.toImmutable()).collect(Collectors.toList());
                Optional<ISpell> optSpell = SpellManager.getInstance().getFirstMatch(positions, serverWorld);
                optSpell.ifPresent((spell) -> {
                    spell.cast(entity, positions, serverWorld);

                    Vec3d offPos = Vec3d.of(blockPos.offset(facing.getOpposite()));
                    Vec3d vec = offPos.subtract(Vec3d.of(blockPos)).multiply(blockEntity.spellLength / 3);
                    ItemEntityHelper.spawnItem(serverWorld, offPos.add(0.5, 0.5, 0.5), spell.craft(null), vec);

                    doCompletionFX(serverWorld, blockPos, blockState, blockEntity);
                });

                blockEntity.setPhase(Phase.RESTING);
            } else if (blockEntity.isResting()) {
                blockEntity.phaseTicks++;
                if (blockEntity.phaseTicks > RNF.serverConfig().TRANSCRIBE.ACTION_COOLDOWN) {
                    blockEntity.becomeDormant();
                }
            }
        } else if (!powered && !blockEntity.isDormant()) {
            blockEntity.becomeDormant();
        }

        if (!blockEntity.isResting()) {
            spawnParticles(serverWorld, blockPos, blockState, blockEntity);
        }
    }

    protected static void doTranscribeFX(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        if (blockEntity.phaseTicks % RNF.serverConfig().TRANSCRIBE.ACTION_TICKS_PER_LENGTH == 0) {
            Direction facing = blockState.get(Properties.FACING);
            int offset = blockEntity.phaseTicks / RNF.serverConfig().TRANSCRIBE.ACTION_TICKS_PER_LENGTH;
            BlockPos offsetPos = blockPos.offset(facing, offset);

            double x = offsetPos.getX() + 0.5;
            double y = offsetPos.getY() + 0.5;
            double z = offsetPos.getZ() + 0.5;

            serverWorld.playSound(null, x, y, z, RNFSounds.RITUAL_GENERIC_PROGRESS.get(), SoundCategory.BLOCKS, 1F, 1F);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, x, y, z, 3, 0, 0, 0, 0.1);
            serverWorld.spawnParticles(ParticleTypes.FLASH, x, y, z, 0, 0, 0, 0, 0);
        }
    }

    protected static void doPrimedFX(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        Direction facing = blockState.get(Properties.FACING).getOpposite();

        BlockPos target = blockPos.offset(facing, RNF.serverConfig().TRANSCRIBE.PRIMED_TRIGGER_DISTANCE).subtract(blockPos);

        double x = blockPos.getX() + 0.5;
        double y = blockPos.getY() + 0.5;
        double z = blockPos.getZ() + 0.5;

        int scale = 3;
        serverWorld.spawnParticles(RNFParticleTypes.END_ROD_REV.get(), x, y, z, 10, (target.getX() / scale), (target.getY() / scale), (target.getZ() / scale), 0.1);
    }

    protected static void doCompletionFX(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        int spellLength = blockEntity.spellLength;
        Direction facing = blockState.get(Properties.FACING).getOpposite();

        BlockPos flash = blockPos.offset(facing);
        BlockPos target = blockPos.offset(facing, spellLength).subtract(blockPos);

        double x = blockPos.getX() + 0.5;
        double y = blockPos.getY() + 0.5;
        double z = blockPos.getZ() + 0.5;

        int scale = 3;
        serverWorld.playSound(null, x, y, z, RNFSounds.RITUAL_GENERIC_COMPLETE.get(), SoundCategory.BLOCKS, 1F, 1F);
        serverWorld.spawnParticles(ParticleTypes.FLASH, flash.getX() + 0.5, flash.getY() + 0.5, flash.getZ() + 0.5, 0, 0, 0, 0, 0);
        serverWorld.spawnParticles(RNFParticleTypes.END_ROD_REV.get(), x, y, z, 50, (target.getX() / scale), (target.getY() / scale), (target.getZ() / scale), 0.1);
    }

    protected static void spawnParticles(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, RitualTranscriberBlockEntity blockEntity) {
        Direction facing = blockState.get(Properties.FACING).getOpposite();

        final int count = 1;
        final float speed = 0.25f;

        double x = blockPos.getX() + 0.5 - (facing.getOffsetX() * 0.5) + (facing.getOffsetX());
        double y = blockPos.getY() + 0.5 - (facing.getOffsetY() * 0.5) + (facing.getOffsetY());
        double z = blockPos.getZ() + 0.5 - (facing.getOffsetZ() * 0.5) + (facing.getOffsetZ());

        serverWorld.spawnParticles(blockEntity.isTranscribing() ? RNFParticleTypes.END_ROD_REV.get() : RNFParticleTypes.ENCHANT_NG_REV.get(), x, y, z, count, 0, 0, 0, speed);
    }

    public static Phase getPhaseForSpellType(Spell spellType) {
        Phase phase;
        switch (spellType) {
            case PRIMING:
                phase = Phase.PRIMED;
                break;
            default:
                phase = Phase.COMPLETE;
        };
        return phase;
    }

    public boolean isDormant() { return getPhase() == Phase.DORMANT; }
    public boolean isActive() { return getPhase() == Phase.ACTIVE; }
    public boolean isTranscribing() { return getPhase() == Phase.TRANSCRIBING; }
    public boolean isPrimed() { return getPhase() == Phase.PRIMED; }
    public boolean isCompleting() { return getPhase() == Phase.COMPLETE; }
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
        this.spellLength = 0;
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
        if (nbtCompound.contains("SpellLength"))
            spellLength = nbtCompound.getInt("SpellLength");
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
        if (spellLength >= 0)
            nbtCompound.putInt("SpellLength", spellLength);
        if (castTime >= 0)
            nbtCompound.putInt("CastTime", castTime);
        if (hasOutput)
            nbtCompound.putBoolean("HasOutput", true);
    }
}
