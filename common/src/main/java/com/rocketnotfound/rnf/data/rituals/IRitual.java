package com.rocketnotfound.rnf.data.rituals;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;

public interface IRitual extends Recipe<Inventory> {
    Ritual getRitualType();

    @Override
    default boolean isIgnoredInRecipeBook() {
        return true;
    }
}
