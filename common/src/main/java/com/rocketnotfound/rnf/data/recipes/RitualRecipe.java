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

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RitualRecipe implements IRitualRecipe {
    public static final Identifier TYPE = createIdentifier("ritual");

    protected final Identifier id;
    protected final ItemStack output;
    protected final DefaultedList<Ingredient> recipeItems;
    protected final boolean requiresLoop;

    public RitualRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, boolean requiresLoop) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.requiresLoop = requiresLoop;
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

    public boolean requiresLoop() {
        return requiresLoop;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return output;
    }

    @Override
    public boolean fits(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNFRecipes.RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFRecipes.RITUAL_TYPE.get();
    }

    public static class RitualRecipeType implements RecipeType<RitualRecipe> {
        @Override
        public String toString() {
            return "rnf:ritual";
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<RitualRecipe> {
        @Override
        public RitualRecipe read(Identifier identifier, JsonObject jsonObject) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "output"));
            boolean requiresLoop = JsonHelper.getBoolean(jsonObject, "requiresLoop");

            JsonArray ingredients = JsonHelper.getArray(jsonObject, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new RitualRecipe(identifier, output, inputs, requiresLoop);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, RitualRecipe recipe) {
            packetByteBuf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(recipe.getOutput());
            packetByteBuf.writeBoolean(recipe.requiresLoop());
        }

        @Nullable
        @Override
        public RitualRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(packetByteBuf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack output = packetByteBuf.readItemStack();
            boolean requiresLoop = packetByteBuf.readBoolean();

            return new RitualRecipe(identifier, output,
                    inputs, requiresLoop);
        }
    }
}
