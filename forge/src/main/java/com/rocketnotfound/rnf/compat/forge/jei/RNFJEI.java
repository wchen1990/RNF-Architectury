package com.rocketnotfound.rnf.compat.forge.jei;

import com.google.common.base.Predicates;
import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.compat.forge.jei.category.NormalRitualCategory;
import com.rocketnotfound.rnf.compat.forge.jei.category.RNFRecipeCategory;
import com.rocketnotfound.rnf.data.managers.RitualManager;
import com.rocketnotfound.rnf.data.rituals.RNFRituals;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

@JeiPlugin
@SuppressWarnings("unused")
public class RNFJEI implements IModPlugin {
    private static final Identifier ID = createIdentifier("jei_plugin");

    public IIngredientManager ingredientManager;
    private final List<RNFRecipeCategory> allCategories = new ArrayList<>();

    public void loadCategories(IRecipeCategoryRegistration registration) {
        allCategories.clear();
        allCategories.add(new NormalRitualCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories(registration);
        registration.addRecipeCategories(allCategories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ingredientManager = registration.getIngredientManager();
        allCategories.forEach(c -> registration.addRecipes(c.getRecipeType(), c.getRecipes()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> registration.addRecipeCatalyst((ItemStack) c.getCatalyst(), c.getRecipeType()));
    }

    @Override
    @Nonnull
    public Identifier getPluginUid() {
        return ID;
    }
}
