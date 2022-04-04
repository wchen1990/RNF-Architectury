package com.rocketnotfound.rnf.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.architectury.core.AbstractRecipeSerializer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.Random;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class LoopRitualRecipe implements IRitualRecipe {
    public static final Identifier TYPE = createIdentifier("loop_ritual");

    protected final Identifier id;
    protected final Ingredient output;
    protected final DefaultedList<Ingredient> recipeItems;

    protected final Random random;

    public LoopRitualRecipe(Identifier id, Ingredient output, DefaultedList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.random = new Random();
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (inventory.size() == recipeItems.size()) {
            boolean matches = true;
            int size = recipeItems.size();
            for (int idx = 0; idx < size; ++idx) {
                matches = matches && recipeItems.get(idx).test(inventory.getStack(idx));
                if (!matches) break;
            }
            return matches;
        }

        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return getOutput();
    }

    @Override
    public boolean fits(int i, int j) {
        return true;
    }

    public Ingredient getOutputIngredient() {
        return output;
    }

    @Override
    public ItemStack getOutput() {
        ItemStack[] outcomes = output.getMatchingStacks();
        if (outcomes.length > 0) {
            return outcomes[random.nextInt(outcomes.length)].copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNFRecipes.LOOP_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFRecipes.LOOP_RITUAL_TYPE.get();
    }

    public static class LoopRitualRecipeType implements RecipeType<LoopRitualRecipe> {
        @Override
        public String toString() {
            return "rnf:loop_ritual";
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<LoopRitualRecipe> {
        @Override
        public LoopRitualRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient output = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "output"));

            JsonArray ingredients = JsonHelper.getArray(jsonObject, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new LoopRitualRecipe(identifier, output, inputs);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, LoopRitualRecipe recipe) {
            packetByteBuf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(packetByteBuf);
            }
            recipe.getOutputIngredient().write(packetByteBuf);
        }

        @Nullable
        @Override
        public LoopRitualRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(packetByteBuf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(packetByteBuf));
            }

            Ingredient output = Ingredient.fromPacket(packetByteBuf);

            return new LoopRitualRecipe(identifier, output, inputs);
        }
    }
}
