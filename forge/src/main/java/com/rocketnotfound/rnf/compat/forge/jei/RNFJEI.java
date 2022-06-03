package com.rocketnotfound.rnf.compat.forge.jei;

import com.rocketnotfound.rnf.compat.forge.jei.category.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

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
        allCategories.add(new CircleRitualCategory(registration.getJeiHelpers().getGuiHelper()));
        allCategories.add(new EngravingRitualCategory(registration.getJeiHelpers().getGuiHelper()));
        allCategories.add(new AnchorRitualCategory(registration.getJeiHelpers().getGuiHelper()));
        allCategories.add(new TetheredRitualCategory(registration.getJeiHelpers().getGuiHelper()));
        allCategories.add(new InfusionRitualCategory(registration.getJeiHelpers().getGuiHelper()));
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
