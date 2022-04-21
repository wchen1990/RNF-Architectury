package com.rocketnotfound.rnf.data.spells;

import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.util.SpellHelper;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ISpell extends Recipe<Inventory> {
    int getLength();
    Spell getSpellType();
    List<Pair<String, BlockStateArgument>> getInitialState();
    List<Pair<String, BlockState>> getFinalState();

    default List<Pair<String, Optional<NbtCompound>>> getEffects() {
        return Collections.emptyList();
    }
    default boolean matches(List<BlockPos> positions, ServerWorld world) {
        List<Pair<String, BlockStateArgument>> initialState = getInitialState();
        List<BlockPos> posCopy = new ArrayList<>(positions);
        if (initialState.size() > 0 && posCopy.size() > 1 && initialState.size() < posCopy.size()) {
            if(world.getBlockState(posCopy.get(posCopy.size() - 1)).isOf(RNFBlocks.RITUAL_TRANSCRIBER.get())) {
                Collections.reverse(posCopy);
            }

            boolean matches = true;
            for (int idx = 0; idx < initialState.size(); ++idx) {
                BlockStateArgument bsa = initialState.get(idx).getRight();
                matches = matches && bsa.test(world, posCopy.get(idx + 1));
                if (!matches) break;
            }

            return matches;
        }
        return false;
    }
    default boolean cast(@Nullable LivingEntity livingEntity, List<BlockPos> positions, ServerWorld world) {
        if (matches(positions, world)) {
            boolean reverse = false;

            BlockPos transcriberPosition;
            if (world.getBlockState(positions.get(positions.size() - 1)).isOf(RNFBlocks.RITUAL_TRANSCRIBER.get())) {
                transcriberPosition = positions.get(positions.size() - 1);
                reverse = true;
            } else {
                transcriberPosition = positions.get(0);
            }

            List<Pair<String, BlockState>> finalState = getFinalState();
            if (finalState.size() > 0) {
                List<BlockPos> posCopy = new ArrayList<>(positions);
                if (reverse) {
                    Collections.reverse(posCopy);
                }

                for (int idx = 0; idx < finalState.size(); ++idx) {
                    world.setBlockState(posCopy.get(idx + 1), finalState.get(idx).getRight());
                }
            }

            LivingEntity targetEntity = livingEntity;
            for (Pair<String, Optional<NbtCompound>> effect : getEffects()) {
                SpellEffects.SpellEffectDeserialize spell = SpellEffects.TYPE_MAP.getOrDefault(effect.getLeft(), null);
                if (spell != null) {
                    NbtCompound nbt = effect.getRight().orElseGet(() -> new NbtCompound()).copy();
                    if (SpellHelper.processNbtForDeserialization(nbt, world, this, transcriberPosition)) {
                        if (spell.requiresEntity()) {
                            if (targetEntity != null) {
                                targetEntity = spell.deserialize(nbt).cast(world, targetEntity);
                            }
                        } else {
                            spell.deserialize(nbt).cast(world, null);
                        }
                    } else {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    // The following default overrides are an abuse of the recipe and recipe type registries
    // since we haven't created our own registries for these things
    @Override
    default boolean matches(Inventory inventory, World world) {
        return false;
    }

    @Override
    default ItemStack craft(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean fits(int i, int j) {
        return true;
    }

    @Override
    default ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isIgnoredInRecipeBook() {
        return true;
    }
}
