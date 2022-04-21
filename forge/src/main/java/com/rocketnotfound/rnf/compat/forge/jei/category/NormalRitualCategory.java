package com.rocketnotfound.rnf.compat.forge.jei.category;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.data.managers.RitualManager;
import com.rocketnotfound.rnf.data.rituals.NormalRitual;
import com.rocketnotfound.rnf.data.rituals.RNFRituals;
import com.rocketnotfound.rnf.item.RNFItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public class NormalRitualCategory extends RNFRecipeCategory<NormalRitual> {
    // GUI Constants
    protected final static int maxWidth = 180;
    protected final static int maxCraftWidth = 142;
    protected final static int maxRows = 2;

    protected final static int slotSize = 16;
    protected final static int xSpacing = 2;
    protected final static int ySpacing = 2;
    protected final static int catalystYSpacing = 10;

    protected final static int maxHeight = ((slotSize + catalystYSpacing) * maxRows) + (ySpacing * (maxRows - 1));

    public NormalRitualCategory(IGuiHelper helper) {
        super(
            "normal_ritual",
            helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, RNFBlocks.RITUAL_FRAME.get().asItem().getDefaultStack()),
            emptyBackground(maxWidth, maxHeight)
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, NormalRitual recipe, IFocusGroup focuses) {
        int recipeSize = recipe.getIngredients().size();

        int xSpaceTaken = ((recipeSize * slotSize) + ((recipeSize - 1) * xSpacing));
        int numRows = 1 + xSpaceTaken / maxCraftWidth;
        int calcHeight = (numRows * (slotSize + catalystYSpacing)) + ((numRows - 1) * ySpacing);

        int xPlacement = (numRows > 1) ? 0 : (maxCraftWidth - xSpaceTaken) / 2;
        int yPlacement = (maxHeight - calcHeight) / 2;
        for(Ingredient ing : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, xPlacement, yPlacement + catalystYSpacing).addItemStack(getCatalyst());
            builder.addSlot(RecipeIngredientRole.INPUT, xPlacement, yPlacement).addIngredients(ing);

            xPlacement += slotSize + xSpacing;
            if (xPlacement >= maxCraftWidth) {
                xPlacement = xPlacement % maxCraftWidth;
                yPlacement += (slotSize + catalystYSpacing) + ySpacing;
            }
        }

        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            ((maxWidth - maxCraftWidth - slotSize) / 2) + maxCraftWidth,
            (maxHeight - slotSize) / 2
        ).addItemStack(recipe.getOutput());
    }

    @Override
    @SuppressWarnings("removal")
    public Class<? extends NormalRitual> getRecipeClass() {
        return NormalRitual.class;
    }

    @Override
    public ItemStack getCatalyst() {
        return RNFItems.RITUAL_FRAME.get().getDefaultStack();
    }

    @Override
    public List<NormalRitual> getRecipes() {
        return RitualManager.getInstance().listAllOfType(RNFRituals.RITUAL_TYPE.get());
    }

    @Override
    public RecipeType<NormalRitual> getRecipeType() {
        return RecipeType.create(RNF.MOD_ID, name, NormalRitual.class);
    }
}
