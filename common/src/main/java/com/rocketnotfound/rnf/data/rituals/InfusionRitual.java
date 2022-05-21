package com.rocketnotfound.rnf.data.rituals;

import com.google.gson.JsonObject;
import com.rocketnotfound.rnf.util.BlockStateParser;
import dev.architectury.core.AbstractRecipeSerializer;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class InfusionRitual implements IRitual, IInfusionRitual {
    public static final Identifier TYPE = createIdentifier("infusion_ritual");

    protected final Identifier id;
    protected final ItemStack output;
    protected final DefaultedList<Ingredient> recipeItems;
    protected final Pair<String, BlockStateArgument> initialState;
    protected final Pair<String, BlockStateArgument> finalState;

    public InfusionRitual(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems, Pair<String, BlockStateArgument> initialState, Pair<String, BlockStateArgument> finalState) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.initialState = initialState;
        this.finalState = finalState;
    }

    public Ritual getRitualType() {
        return Ritual.INFUSION;
    }

    public boolean isValid() {
        return initialState != null && finalState != null && initialState.getRight() != null && finalState.getRight() != null;
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
    public DefaultedList<Ingredient> getIngredients() {
        return recipeItems;
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
        return RNFRituals.INFUSION_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFRituals.INFUSION_RITUAL_TYPE.get();
    }

    @Override
    public Pair<String, BlockStateArgument> getTargetPair() {
        return initialState;
    }

    @Override
    public Pair<String, BlockStateArgument> getResultPair() {
        return finalState;
    }

    public static class RitualType implements RecipeType<InfusionRitual> {
        @Override
        public String toString() {
            return TYPE.toString();
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<InfusionRitual> {
        @Override
        public InfusionRitual read(Identifier identifier, JsonObject jsonObject) {
            ItemStack output = (JsonHelper.hasJsonObject(jsonObject,"output")) ?
                    ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "output")) : ItemStack.EMPTY;

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(1, Ingredient.fromJson(JsonHelper.getObject(jsonObject, "catalyst")));

            String initial = JsonHelper.getString(jsonObject, "initial");
            Pair<String, BlockStateArgument> initialPair = new Pair(initial, BlockStateParser.parse(initial));
            String after = JsonHelper.getString(jsonObject, "after");
            Pair<String, BlockStateArgument> afterPair = new Pair(after, BlockStateParser.parse(after));

            return new InfusionRitual(identifier, output, inputs, initialPair, afterPair);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, InfusionRitual recipe) {
            packetByteBuf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(recipe.getOutput());
            packetByteBuf.writeString(recipe.getTargetPair().getLeft());
            packetByteBuf.writeString(recipe.getResultPair().getLeft());
        }

        @Nullable
        @Override
        public InfusionRitual read(Identifier identifier, PacketByteBuf packetByteBuf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(packetByteBuf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack output = packetByteBuf.readItemStack();

            String initial = packetByteBuf.readString();
            Pair<String, BlockStateArgument> initialPair = new Pair(initial, BlockStateParser.parse(initial));
            String after = packetByteBuf.readString();
            Pair<String, BlockStateArgument> afterPair = new Pair(after, BlockStateParser.parse(after));

            return new InfusionRitual(identifier, output, inputs, initialPair, afterPair);
        }
    }
}
