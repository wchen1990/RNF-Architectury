package com.rocketnotfound.rnf.compat.forge.jei.category;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.client.gui.RNFGuiTextures;
import com.rocketnotfound.rnf.data.managers.RitualManager;
import com.rocketnotfound.rnf.data.rituals.InfusionRitual;
import com.rocketnotfound.rnf.data.rituals.NormalRitual;
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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class InfusionRitualCategory extends RNFRecipeCategory<InfusionRitual> {
    // GUI Constants
    protected final static int maxWidth = 180;
    protected final static int maxCraftWidth = 142;
    protected final static int maxRows = 2;

    protected final static int slotSize = 16;
    protected final static int xSpacing = 16;
    protected final static int ySpacing = 2;
    protected final static int catalystYSpacing = 10;

    protected final static int maxHeight = ((slotSize + catalystYSpacing + catalystYSpacing) * maxRows) + (ySpacing * (maxRows - 1));

    public InfusionRitualCategory(IGuiHelper helper) {
        super(
            InfusionRitual.TYPE.getPath(),
            helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, RNFBlocks.RITUAL_FRAME.get().asItem().getDefaultStack()),
            emptyBackground(maxWidth, maxHeight),
            helper
        );
    }

    protected void calculateSpacing(InfusionRitual recipe, BasedOnXYCalculationWithIdx based) {
        // Infusions only have 1 catalyst, 1 block input, and 1 block output
        int recipeSize = 3;

        int xSpaceTaken = ((recipeSize * slotSize) + ((recipeSize - 1) * xSpacing));
        int numRows = (xSpaceTaken > maxCraftWidth) ? 1 + xSpaceTaken / maxCraftWidth : 1;
        int calcHeight = (numRows * (slotSize + catalystYSpacing + catalystYSpacing)) + ((numRows - 1) * ySpacing);

        int xPlacement = (numRows > 1) ? 0 : (maxCraftWidth - xSpaceTaken) / 2;
        int yPlacement = (maxHeight - calcHeight) / 2;
        for(int idx = 0; idx < recipeSize; ++idx) {
            based.execute(xPlacement, yPlacement, idx);

            xPlacement += slotSize + xSpacing;
            if (xPlacement >= maxCraftWidth) {
                xPlacement = xPlacement % maxCraftWidth;
                yPlacement += (slotSize + catalystYSpacing + catalystYSpacing) + ySpacing;
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InfusionRitual recipe, IFocusGroup focuses) {
        final List<Ingredient> ingredients = recipe.getIngredients();

        calculateSpacing(recipe, (xPlacement, yPlacement, idx) -> {
            if (idx == 0 ) {
                builder
                    .addSlot(RecipeIngredientRole.INPUT, xPlacement, yPlacement)
                    .addItemStack(recipe.getTargetPair().getRight().getBlockState().getBlock().asItem().getDefaultStack());
            } else if (idx == 1) {
                builder
                    .addSlot(RecipeIngredientRole.INPUT, xPlacement, yPlacement)
                    .addIngredients(ingredients.get(0))
                    .addTooltipCallback((view, tooltip) -> {
                        tooltip.add(new TranslatableText("recipe.rnf.generic.radius", recipe.getSearchRadius()));
                        tooltip.add(new TranslatableText("recipe.rnf.generic.num_times", recipe.getNumInfusions()));
                    });
            } else if (idx == 2) {
                builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPlacement, yPlacement)
                    .addItemStack(recipe.getResultPair().getRight().getBlockState().getBlock().asItem().getDefaultStack());
            }
        });

        if (!recipe.getOutput().isEmpty()) {
            builder.addSlot(
                RecipeIngredientRole.OUTPUT,
                ((maxWidth - maxCraftWidth - slotSize) / 2) + maxCraftWidth,
                (maxHeight - slotSize) / 2
            ).addItemStack(recipe.getOutput());
        }
    }

    @Override
    public void draw(InfusionRitual recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
        calculateSpacing(recipe, (xPlacement, yPlacement, idx) -> {
            int yPlaceMod = yPlacement + catalystYSpacing;
            if (idx == 0) {
                RNFGuiTextures.DOWN_TO_RIGHT.render(stack, xPlacement - 1, yPlaceMod);
            } else if (idx == 1) {
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, getCatalyst()).draw(stack, xPlacement, yPlacement + catalystYSpacing);
                RNFGuiTextures.CONNECT.render(stack, xPlacement - xSpacing, yPlaceMod);
                RNFGuiTextures.CONNECT.render(stack, xPlacement, yPlaceMod);
                RNFGuiTextures.CONNECT.render(stack, xPlacement + xSpacing, yPlaceMod);
            } else if (idx == 2) {
                RNFGuiTextures.UP_FROM_LEFT.render(stack, xPlacement, yPlaceMod);
            }
        });

        RNFGuiTextures.SLOT.render(
            stack,
            ((maxWidth - maxCraftWidth - slotSize) / 2) + maxCraftWidth - (RNFGuiTextures.SLOT.width - slotSize) / 2,
            (maxHeight - slotSize) / 2  - (RNFGuiTextures.SLOT.height - slotSize) / 2
        );

        if (recipe.getOutput().isEmpty()) {
            RNFGuiTextures.X.render(
                stack,
                ((maxWidth - maxCraftWidth - slotSize) / 2) + maxCraftWidth - (RNFGuiTextures.SLOT.width - slotSize) / 2,
                (maxHeight - slotSize) / 2  - (RNFGuiTextures.X.height - slotSize) / 2
            );
        }
    }

    @Override
    @SuppressWarnings("removal")
    public Class<? extends InfusionRitual> getRecipeClass() {
        return InfusionRitual.class;
    }

    @Override
    public ItemStack getCatalyst() {
        return RNFItems.RITUAL_FRAME.get().getDefaultStack();
    }

    @Override
    public List<InfusionRitual> getRecipes() {
        return RitualManager.getInstance().listAllOfType(RNFRituals.INFUSION_RITUAL_TYPE.get());
    }

    @Override
    public RecipeType<InfusionRitual> getRecipeType() {
        return RecipeType.create(RNF.MOD_ID, name, InfusionRitual.class);
    }
}
