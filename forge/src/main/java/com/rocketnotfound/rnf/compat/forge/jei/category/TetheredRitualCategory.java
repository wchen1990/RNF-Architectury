package com.rocketnotfound.rnf.compat.forge.jei.category;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.client.gui.RNFGuiTextures;
import com.rocketnotfound.rnf.data.managers.RitualManager;
import com.rocketnotfound.rnf.data.rituals.RNFRituals;
import com.rocketnotfound.rnf.data.rituals.TetheredRitual;
import com.rocketnotfound.rnf.item.RNFItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.block.Block;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TetheredRitualCategory extends RNFRecipeCategory<TetheredRitual> {
    // GUI Constants
    protected final static int maxWidth = 180;
    protected final static int maxCraftWidth = 142;
    protected final static int maxRows = 2;

    protected final static int slotSize = 16;
    protected final static int xSpacing = 2;
    protected final static int ySpacing = 2;
    protected final static int catalystYSpacing = 10;

    protected final static int maxHeight = ((slotSize + catalystYSpacing * 2 + ySpacing + slotSize * 2) * maxRows) + (ySpacing * (maxRows - 1));

    public TetheredRitualCategory(IGuiHelper helper) {
        super(
                TetheredRitual.TYPE.getPath(),
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, RNFBlocks.RITUAL_FRAME.get().asItem().getDefaultStack()),
                emptyBackground(maxWidth, maxHeight),
                helper
        );
    }

    protected void calculateSpacing(TetheredRitual recipe, BasedOnXYCalculationWithIdx based) {
        int recipeSize = recipe.getIngredients().size() + 2;

        int xSpaceTaken = ((recipeSize * slotSize) + ((recipeSize - 1) * xSpacing));
        int numRows = 1 + xSpaceTaken / maxCraftWidth;
        int calcHeight = (numRows * (slotSize + catalystYSpacing * 2 + ySpacing + slotSize * 2)) + ((numRows - 1) * ySpacing);

        int xPlacement = (numRows > 1) ? 0 : (maxCraftWidth - xSpaceTaken) / 2;
        int yPlacement = (maxHeight - calcHeight) / 2;
        for(int idx = 0; idx < recipeSize; ++idx) {
            based.execute(xPlacement, yPlacement, idx);

            xPlacement += slotSize + xSpacing;
            if (xPlacement >= maxCraftWidth) {
                xPlacement = xPlacement % maxCraftWidth;
                yPlacement += (slotSize + catalystYSpacing * 2 + ySpacing + slotSize * 2) + ySpacing;
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TetheredRitual recipe, IFocusGroup focuses) {
        final List<Ingredient> ingredients = recipe.getIngredients();

        Pair<Block, String> base = recipe.getBase();
        Pair<Block, String> anchor = recipe.getAnchor();

        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(base.getLeft().asItem().getDefaultStack());
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStack(anchor.getLeft().asItem().getDefaultStack());

        final List<ItemStack> baseOutputs;
        String identifierReplace = base.getRight();
        if (identifierReplace.contains("%s")) {
            baseOutputs = IntStream.rangeClosed('a', 'z')
                .mapToObj((character) ->
                    ForgeRegistries.BLOCKS.getValue(
                        new Identifier(String.format(base.getRight(), (char) character))
                    )
                )
                .map((block) -> block.asItem().getDefaultStack())
                .filter((itemStack) -> !itemStack.isEmpty())
                .collect(Collectors.toList());
        } else {
            baseOutputs = DefaultedList.ofSize(1,
                ForgeRegistries.BLOCKS.getValue(new Identifier(base.getRight())).asItem().getDefaultStack()
            );
        }

        final List<ItemStack> anchorOutputs;
        if (anchor.getRight().contains("%s")) {
            anchorOutputs = IntStream.rangeClosed('a', 'z')
                .mapToObj((character) ->
                    ForgeRegistries.BLOCKS.getValue(
                        new Identifier(String.format(anchor.getRight(), (char) character))
                    )
                )
                .map((block) -> block.asItem().getDefaultStack())
                .filter((itemStack) -> !itemStack.isEmpty())
                .collect(Collectors.toList());
        } else {
            anchorOutputs = DefaultedList.ofSize(1,
                ForgeRegistries.BLOCKS.getValue(new Identifier(anchor.getRight())).asItem().getDefaultStack()
            );
        }

        calculateSpacing(recipe, (xPlacement, yPlacement, idx) -> {
            if (idx == 0 || idx == ingredients.size() + 1) {
                builder.addSlot(
                    RecipeIngredientRole.OUTPUT,
                    xPlacement,
                    yPlacement + catalystYSpacing * 2 + ySpacing + slotSize * 2
                ).addItemStacks(idx == 0 ? baseOutputs : anchorOutputs)
                .addTooltipCallback((view, tooltip) -> {
                    tooltip.add(new TranslatableText(String.format("recipe.rnf.generic.%s", idx == 0 ? "alter_base" : "alter_anchor")));
                });
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, xPlacement, yPlacement).addIngredients(ingredients.get(idx - 1));
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
    public void draw(TetheredRitual recipe, IRecipeSlotsView recipeSlotsView, MatrixStack stack, double mouseX, double mouseY) {
        final List<Ingredient> ingredients = recipe.getIngredients();
        final int size = ingredients.size();

        calculateSpacing(recipe, (xPlacement, yPlacement, idx) -> {
            helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, getCatalyst()).draw(stack, xPlacement, yPlacement + catalystYSpacing);

            if (idx == 0) {
                helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, recipe.getBase().getLeft().asItem().getDefaultStack())
                        .draw(stack, xPlacement, yPlacement + catalystYSpacing * 2);

                RNFGuiTextures.STAR.render(stack, xPlacement - 1, yPlacement - 1);
                RNFGuiTextures.POINT_DOWN.render(stack, xPlacement - 1, yPlacement + catalystYSpacing * 2 + slotSize);
                RNFGuiTextures.SLOT.render(stack, xPlacement - 1, yPlacement + catalystYSpacing * 2 + slotSize * 2 + 1);
            }

            if (size > 1) {
                int yPlaceMod = yPlacement + catalystYSpacing * 2;
                if (idx == size + 1) {
                    helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, recipe.getAnchor().getLeft().asItem().getDefaultStack())
                            .draw(stack, xPlacement, yPlacement + catalystYSpacing * 2);

                    RNFGuiTextures.STAR.render(stack, xPlacement - 1, yPlacement - 1);
                    RNFGuiTextures.POINT_DOWN.render(stack, xPlacement - 1, yPlacement + catalystYSpacing * 2 + slotSize);
                    RNFGuiTextures.SLOT.render(stack, xPlacement - 1, yPlacement + catalystYSpacing * 2 + slotSize * 2 + 1);
                } else if (idx > 0) {
                    RNFGuiTextures.DOWN_CONNECT.render(stack, xPlacement - 1, yPlaceMod);
                }
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
    public Class<? extends TetheredRitual> getRecipeClass() {
        return TetheredRitual.class;
    }

    @Override
    public ItemStack getCatalyst() {
        return RNFItems.RITUAL_FRAME.get().getDefaultStack();
    }

    @Override
    public List<TetheredRitual> getRecipes() {
        return RitualManager.getInstance().listAllOfType(RNFRituals.TETHER_RITUAL_TYPE.get());
    }

    @Override
    public RecipeType<TetheredRitual> getRecipeType() {
        return RecipeType.create(RNF.MOD_ID, name, TetheredRitual.class);
    }
}
