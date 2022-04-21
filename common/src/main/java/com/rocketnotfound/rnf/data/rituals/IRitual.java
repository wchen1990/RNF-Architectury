package com.rocketnotfound.rnf.data.rituals;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;

public interface IRitual extends Recipe<Inventory> {
    Ritual getRitualType();
    DefaultedList<Ingredient> getIngredients();

    @Override
    default boolean isIgnoredInRecipeBook() {
        return true;
    }
}
