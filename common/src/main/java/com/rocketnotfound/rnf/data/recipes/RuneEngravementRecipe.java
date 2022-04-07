package com.rocketnotfound.rnf.data.recipes;

import com.google.gson.*;
import com.rocketnotfound.rnf.RNF;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RuneEngravementRecipe implements IRitualRecipe {
    public static final Identifier TYPE = createIdentifier("rune_engravement");

    protected final Inventory recipeItems;
    protected final Random random;

    public RuneEngravementRecipe(Inventory recipeItems) {
        this.recipeItems = recipeItems;
        this.random = new Random();
    }

    protected static JsonArray getRecipeJson() {
        Gson gson = new Gson();
        JsonArray customRecipe;

        try {
            customRecipe = gson.fromJson(RNF.serverConfig().RUNE_ENGRAVING_RECIPE, JsonArray.class);
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("Invalid rune engravement json config");
        }

        return customRecipe;
    }

    public static boolean isValid(Inventory inventory, World world) {
        JsonArray customRecipe = getRecipeJson();
        if (customRecipe != null) {
            int size = customRecipe.size();

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(customRecipe.size(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromJson(customRecipe.get(i)));
            }

            if (inventory.size() == size + 1) {
                boolean matches = true;
                for (int idx = 0; idx < size; ++idx) {
                    matches = matches && inputs.get(idx).test(inventory.getStack(idx));
                    if (!matches) break;
                }
                return matches && !inventory.getStack(size).isEmpty();
            }
        }

        return false;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return RuneEngravementRecipe.isValid(inventory, world);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        if (matches(inventory, null)) {
            Identifier id = Registry.ITEM.getId(inventory.getStack(getRecipeJson().size()).getItem());
            if (id != null) {
                String validLetters = id.getPath().toLowerCase().replaceAll("[^a-z]", "");
                return Registry.ITEM.get(
                    createIdentifier(
                        String.format(
                            "rune_block_%s",
                            validLetters.charAt(random.nextInt(validLetters.length()))
                        )
                    )
                ).getDefaultStack();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return this.craft(this.recipeItems);
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
