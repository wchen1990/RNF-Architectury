package com.rocketnotfound.rnf.data.rituals;

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

public class NormalRitual implements IRitual {
    public static final Identifier TYPE = createIdentifier("ritual");

    protected final Identifier id;
    protected final ItemStack output;
    protected final DefaultedList<Ingredient> recipeItems;

    public NormalRitual(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    public Ritual getRitualType() {
        return Ritual.NORMAL;
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
        return RNFRituals.RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFRituals.RITUAL_TYPE.get();
    }

    public static class RitualType implements RecipeType<NormalRitual> {
        @Override
        public String toString() {
            return TYPE.toString();
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<NormalRitual> {
        @Override
        public NormalRitual read(Identifier identifier, JsonObject jsonObject) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "output"));

            JsonArray ingredients = JsonHelper.getArray(jsonObject, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new NormalRitual(identifier, output, inputs);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, NormalRitual recipe) {
            packetByteBuf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(recipe.getOutput());
        }

        @Nullable
        @Override
        public NormalRitual read(Identifier identifier, PacketByteBuf packetByteBuf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(packetByteBuf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack output = packetByteBuf.readItemStack();

            return new NormalRitual(identifier, output, inputs);
        }
    }
}
