package com.rocketnotfound.rnf.data.spells;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface ISpell extends Recipe<Inventory> {
    Spell getSpellType();
    boolean matches(List<Block> blocks, World world);

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
