package com.rocketnotfound.rnf.compat.forge.jei.category;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.client.gui.RNFGuiTextures;
import com.rocketnotfound.rnf.data.managers.RitualManager;
import com.rocketnotfound.rnf.data.rituals.CircleRitual;
import com.rocketnotfound.rnf.data.rituals.RNFRituals;
import com.rocketnotfound.rnf.item.RNFItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public class CircleRitualCategory extends RNFRecipeCategory<CircleRitual> {
    // GUI Constants
    protected final static int maxWidth = 180;
    protected final static int maxCraftWidth = 142;
    protected final static int maxRows = 2;

    protected final static int slotSize = 16;
    protected final static int xSpacing = 2;
    protected final static int ySpacing = 2;
    protected final static int catalystYSpacing = 10;
    protected final static int secondIndicatorYSpacing = 12;

    protected final static int maxHeight = ((slotSize + catalystYSpacing * 2 + secondIndicatorYSpacing) * maxRows) + (ySpacing * (maxRows - 1));

    public CircleRitualCategory(IGuiHelper helper) {
        super(
            CircleRitual.TYPE.getPath(),
            helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, RNFBlocks.RITUAL_FRAME.get().asItem().getDefaultStack()),
            emptyBackground(maxWidth, maxHeight),
            helper
        );
    }

    protected void calculateSpacing(CircleRitual recipe, BasedOnXYCalculationWithIdx based) {
        int recipeSize = recipe.getIngredients().size();

        int xSpaceTaken = ((recipeSize * slotSize) + ((recipeSize - 1) * xSpacing));
        int numRows = (xSpaceTaken > maxCraftWidth) ? 1 + xSpaceTaken / maxCraftWidth : 1;
        int calcHeight = (numRows * (slotSize + catalystYSpacing * 2 + secondIndicatorYSpacing)) + ((numRows - 1) * ySpacing);

        int xPlacement = (numRows > 1) ? 0 : (maxCraftWidth - xSpaceTaken) / 2;
        int yPlacement = (maxHeight - calcHeight) / 2;
        for(int idx = 0; idx < recipeSize; ++idx) {
            based.execute(xPlacement, yPlacement, idx);

            xPlacement += slotSize + xSpacing;
            if (xPlacement >= maxCraftWidth) {
                xPlacement = xPlacement % maxCraftWidth;
                yPlacement += (slotSize + catalystYSpacing * 2 + secondIndicatorYSpacing) + ySpacing;
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CircleRitual recipe, IFocusGroup focuses) {
        final List<Ingredient> ingredients = recipe.getIngredients();
        calculateSpacing(recipe, (xPlacement, yPlacement, idx) -> {
            builder.addSlot(RecipeIngredientRole.INPUT, xPlacement, yPlacement).addIngredients(ingredients.get(idx));
        });

        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            ((maxWidth - maxCraftWidth - slotSize) / 2) + maxCraftWidth,
            (maxHeight - slotSize) / 2
        ).addIngredients(recipe.getOutputIngredient());
    }

    @Override
    public void draw(CircleRitual recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
        final List<Ingredient> ingredients = recipe.getIngredients();
        final int size = ingredients.size();

        calculateSpacing(recipe, (xPlacement, yPlacement, idx) -> {
            helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, getCatalyst()).draw(stack, xPlacement, yPlacement + catalystYSpacing);

            if (size > 1) {
                int yPlaceMod = yPlacement + catalystYSpacing * 2;
                if (idx == 0) {
                    RNFGuiTextures.DOWN_TO_RIGHT.render(stack, xPlacement - 1, yPlaceMod + secondIndicatorYSpacing);
                    RNFGuiTextures.UP_FROM_RIGHT.render(stack, xPlacement, yPlaceMod);
                } else if (idx == size - 1) {
                    RNFGuiTextures.UP_FROM_LEFT.render(stack, xPlacement, yPlaceMod + secondIndicatorYSpacing);
                    RNFGuiTextures.DOWN_TO_LEFT.render(stack, xPlacement - 1, yPlaceMod);
                } else {
                    RNFGuiTextures.CONNECT.render(stack, xPlacement, yPlaceMod + secondIndicatorYSpacing);
                    RNFGuiTextures.DOWN_CONNECT.render(stack, xPlacement - 1, yPlaceMod);
                }
            }
        });

        RNFGuiTextures.SLOT.render(
            stack,
            ((maxWidth - maxCraftWidth - slotSize) / 2) + maxCraftWidth - (RNFGuiTextures.SLOT.width - slotSize) / 2,
            (maxHeight - slotSize) / 2  - (RNFGuiTextures.SLOT.height - slotSize) / 2
        );
    }

    @Override
    @SuppressWarnings("removal")
    public Class<? extends CircleRitual> getRecipeClass() {
        return CircleRitual.class;
    }

    @Override
    public ItemStack getCatalyst() {
        return RNFItems.RITUAL_FRAME.get().getDefaultStack();
    }

    @Override
    public List<CircleRitual> getRecipes() {
        return RitualManager.getInstance().listAllOfType(RNFRituals.CIRCLE_RITUAL_TYPE.get());
    }

    @Override
    public RecipeType<CircleRitual> getRecipeType() {
        return RecipeType.create(RNF.MOD_ID, name, CircleRitual.class);
    }
}
