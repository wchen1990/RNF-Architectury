package com.rocketnotfound.rnf.data.recipes;

import com.rocketnotfound.rnf.data.Ritual;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;

public interface IRitualRecipe extends Recipe<Inventory> {
    Ritual getRitualType();

    @Override
    default boolean isIgnoredInRecipeBook() {
        return true;
    }
}
