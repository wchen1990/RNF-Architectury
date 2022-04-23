package com.rocketnotfound.rnf.data.rituals;

import com.google.gson.*;
import dev.architectury.core.AbstractRecipeSerializer;
import net.minecraft.block.Block;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class TetheredRitual implements IRitual, IAlterBaseRitual, IAlterAnchorRitual {
    public static final Identifier TYPE = createIdentifier("tethering_ritual");

    protected final Identifier id;
    protected final Pair<Block, String> base;
    protected final Pair<Block, String> anchor;
    protected final DefaultedList<Ingredient> recipeItems;
    protected final ItemStack output;
    protected final Random random;

    public TetheredRitual(Identifier id, Pair<Block, String> base, Pair<Block, String> anchor, DefaultedList<Ingredient> recipeItems, ItemStack output) {
        this.id = id;
        this.base = base;
        this.anchor = anchor;
        this.recipeItems = recipeItems;
        this.output = output;
        this.random = new Random();
    }

    @Override
    public Ritual getRitualType() {
        return Ritual.ENGRAVING;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (inventory.size() == recipeItems.size() + 2) {
            int size = recipeItems.size();

            boolean matches = true;
            for (int idx = 0; idx < size; ++idx) {
                matches = matches && recipeItems.get(idx).test(inventory.getStack(idx + 1));
                if (!matches) break;
            }

            matches = matches && !inventory.getStack(0).isEmpty();
            matches = matches && !inventory.getStack(size).isEmpty();

            return matches;
        }

        return false;
    }

    @Override
    public Block alterBase(Inventory inventory) {
        if (matches(inventory, null)) {
            Identifier id = Registry.ITEM.getId(inventory.getStack(recipeItems.size()).getItem());
            if (id != null) {
                String validLetters = id.getPath().toLowerCase().replaceAll("[^a-z]", "");
                return Registry.BLOCK.get(
                    new Identifier(
                        String.format(
                            base.getRight(),
                            validLetters.charAt(random.nextInt(validLetters.length()))
                        )
                    )
                );
            }
        }
        return base.getLeft();
    }

    @Override
    public Block alterAnchor(Inventory inventory) {
        if (matches(inventory, null)) {
            Identifier id = Registry.ITEM.getId(inventory.getStack(0).getItem());
            if (id != null) {
                String validLetters = id.getPath().toLowerCase().replaceAll("[^a-z]", "");
                return Registry.BLOCK.get(
                    new Identifier(
                        String.format(
                            anchor.getRight(),
                            validLetters.charAt(random.nextInt(validLetters.length()))
                        )
                    )
                );
            }
        }
        return anchor.getLeft();
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
    public Pair<Block, String> getBase() {
        return base;
    }

    @Override
    public Pair<String, String> getBaseStrings() {
        return new Pair<>(Registry.BLOCK.getId(base.getLeft()).toString(), base.getRight());
    }

    @Override
    public Pair<Block, String> getAnchor() {
        return anchor;
    }

    @Override
    public Pair<String, String> getAnchorStrings() {
        return new Pair(Registry.BLOCK.getId(anchor.getLeft()).toString(), anchor.getRight());
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
        return RNFRituals.TETHER_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFRituals.TETHER_RITUAL_TYPE.get();
    }

    public static class RitualType implements RecipeType<TetheredRitual> {
        @Override
        public String toString() {
            return TYPE.toString();
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<TetheredRitual> {
        @Override
        public TetheredRitual read(Identifier identifier, JsonObject jsonObject) {
            JsonObject baseJson = JsonHelper.getObject(jsonObject, "base");
            String initialBase = JsonHelper.getString(baseJson, "initial");
            Pair<Block, String> base = new Pair<>(
                Registry.BLOCK.get(new Identifier(initialBase)),
                (baseJson.has("after")) ? JsonHelper.getString(baseJson, "after") : initialBase
            );

            JsonObject anchorJson = JsonHelper.getObject(jsonObject, "anchor");
            String initialAnchor = JsonHelper.getString(anchorJson, "initial");
            Pair<Block, String> anchor = new Pair<>(
                Registry.BLOCK.get(new Identifier(initialAnchor)),
                (anchorJson.has("after")) ? JsonHelper.getString(anchorJson, "after") : initialAnchor
            );

            ItemStack output = (JsonHelper.hasJsonObject(jsonObject,"output")) ?
                ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "output")) : ItemStack.EMPTY;

            JsonArray ingredients = JsonHelper.getArray(jsonObject, "requirements");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); ++i) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new TetheredRitual(identifier, base, anchor, inputs, output);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, TetheredRitual recipe) {
            Pair<String, String> bases = recipe.getBaseStrings();
            packetByteBuf.writeString(bases.getLeft());
            packetByteBuf.writeString(bases.getRight());

            Pair<String, String> anchors = recipe.getAnchorStrings();
            packetByteBuf.writeString(anchors.getLeft());
            packetByteBuf.writeString(anchors.getRight());

            packetByteBuf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(packetByteBuf);
            }

            packetByteBuf.writeItemStack(recipe.getOutput());
        }

        @Nullable
        @Override
        public TetheredRitual read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Pair<Block, String> base = new Pair<>(
                Registry.BLOCK.get(new Identifier(packetByteBuf.readString())),
                packetByteBuf.readString()
            );

            Pair<Block, String> anchor = new Pair<>(
                Registry.BLOCK.get(new Identifier(packetByteBuf.readString())),
                packetByteBuf.readString()
            );

            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(packetByteBuf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack output = packetByteBuf.readItemStack();

            return new TetheredRitual(identifier, base, anchor, inputs, output);
        }
    }
}
