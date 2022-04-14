package com.rocketnotfound.rnf.data.spells;

import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface ISpell extends Recipe<Inventory> {
    int getLength();
    Spell getSpellType();
    List<Pair<String, BlockStateArgument>> getInitialState();
    boolean matches(List<BlockPos> blocks, ServerWorld world);
    void cast(List<BlockPos> blocks, ServerWorld world);

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
