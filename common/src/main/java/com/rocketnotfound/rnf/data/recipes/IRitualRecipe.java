package com.rocketnotfound.rnf.data.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;

public interface IRitualRecipe extends Recipe<Inventory> {
    @Override
    default boolean isIgnoredInRecipeBook() {
        return true;
    }
}
