package com.rocketnotfound.rnf.data.spells;

import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface ISpell extends Recipe<Inventory> {
    int getLength();
    Spell getSpellType();
    List<Pair<String, BlockStateArgument>> getInitialState();
    List<Pair<String, BlockState>> getFinalState();
    boolean matches(List<BlockPos> blocks, ServerWorld world);
    void cast(@Nullable LivingEntity livingEntity, List<BlockPos> blocks, ServerWorld world);

    default List<ISpellEffects> getEffects() {
        return Collections.emptyList();
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
