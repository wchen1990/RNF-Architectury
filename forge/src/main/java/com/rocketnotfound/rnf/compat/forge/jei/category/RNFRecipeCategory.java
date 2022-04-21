package com.rocketnotfound.rnf.compat.forge.jei.category;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.compat.forge.jei.util.EmptyBackground;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.List;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public abstract class RNFRecipeCategory<T extends Recipe<?>> implements IRecipeCategory<T> {
    protected Identifier uid;
    protected String name;

    protected final IDrawable icon;
    protected final IDrawable background;

    public RNFRecipeCategory(String name, IDrawable icon, IDrawable background) {
        this.uid = createIdentifier(name);
        this.name = name;
        this.icon = icon;
        this.background = background;
    }

    public abstract ItemStack getCatalyst();
    public abstract List<T> getRecipes();
    public abstract RecipeType<T> getRecipeType();

    @Override
    public Identifier getUid() {
        return uid;
    }

    @Override
    public Text getTitle() {
        return new TranslatableText(RNF.MOD_ID + ".recipe." + name);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    public static IDrawable emptyBackground(int width, int height) {
        return new EmptyBackground(width, height);
    }
}
