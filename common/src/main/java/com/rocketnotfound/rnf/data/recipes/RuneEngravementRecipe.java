package com.rocketnotfound.rnf.data.recipes;

import com.google.gson.*;
import com.rocketnotfound.rnf.RNF;
import dev.architectury.core.AbstractRecipeSerializer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RuneEngravementRecipe implements IRitualRecipe {
    public static final Identifier TYPE = createIdentifier("rune_engravement");
    public static final Identifier REQUIREMENTS = createIdentifier("rune_engravement/requirements");

    protected final Identifier id;
    protected final DefaultedList<Ingredient> recipeItems;
    protected final String output;
    protected final Random random;

    public RuneEngravementRecipe(Identifier id, DefaultedList<Ingredient> recipeItems, String output) {
        this.id = id;
        this.recipeItems = recipeItems;
        this.output = output;
        this.random = new Random();
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (inventory.size() == recipeItems.size() + 1) {
            boolean matches = true;
            int size = recipeItems.size();
            for (int idx = 0; idx < size; ++idx) {
                matches = matches && recipeItems.get(idx).test(inventory.getStack(idx));
                if (!matches) break;
            }
            matches = matches && !inventory.getStack(recipeItems.size()).isEmpty();
            return matches;
        }

        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        if (matches(inventory, null)) {
            Identifier id = Registry.ITEM.getId(inventory.getStack(recipeItems.size()).getItem());
            if (id != null) {
                String validLetters = id.getPath().toLowerCase().replaceAll("[^a-z]", "");
                return Registry.ITEM.get(
                    new Identifier(
                        String.format(
                            output,
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
        return ItemStack.EMPTY;
    }

    public String getOutputString() {
        return output;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNFRecipes.RUNE_ENGRAVEMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFRecipes.RUNE_ENGRAVEMENT_TYPE.get();
    }

    public static class RuneEngravementRecipeType implements RecipeType<RuneEngravementRecipe> {
        @Override
        public String toString() {
            return "rnf:rune_engravement";
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<RuneEngravementRecipe> {
        @Override
        public RuneEngravementRecipe read(Identifier identifier, JsonObject jsonObject) {
            String output = JsonHelper.getString(jsonObject, "output");

            JsonArray ingredients = JsonHelper.getArray(jsonObject, "requirements");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new RuneEngravementRecipe(identifier, inputs, output);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, RuneEngravementRecipe recipe) {
            packetByteBuf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(packetByteBuf);
            }
            packetByteBuf.writeString(recipe.getOutputString());
        }

        @Nullable
        @Override
        public RuneEngravementRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(packetByteBuf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(packetByteBuf));
            }

            String output = packetByteBuf.readString();

            return new RuneEngravementRecipe(identifier, inputs, output);
        }
    }
}
