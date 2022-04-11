package com.rocketnotfound.rnf.data.spells;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rocketnotfound.rnf.util.RecipeHelper;
import dev.architectury.core.AbstractRecipeSerializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import java.util.List;
import java.util.Map;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class SingleSpell implements ISpell {
    public static final Identifier TYPE = createIdentifier("ritual");

    protected final Identifier id;
    protected final ItemStack output;
    protected final DefaultedList<Block> requiredBlocks;

    public SingleSpell(Identifier id, ItemStack output, DefaultedList<Block> requiredBlocks) {
        this.id = id;
        this.output = output;
        this.requiredBlocks = requiredBlocks;
    }

    @Override
    public Spell getSpellType() {
        return Spell.SINGLE;
    }

    @Override
    public boolean matches(List<Block> blocks, World world) {
        return false;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    public static class SpellType implements ISpellType<SingleSpell> {
        @Override
        public String toString() {
            return TYPE.toString();
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<SingleSpell> {
        @Override
        public SingleSpell read(Identifier identifier, JsonObject jsonObject) {
            Map<String, Block> map = RecipeHelper.readSymbols(JsonHelper.getObject(jsonObject, "key"), (jsonElement) -> Blocks.AIR, Blocks.AIR);
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "output"));

            JsonArray ingredients = JsonHelper.getArray(jsonObject, "ingredients");
            DefaultedList<Block> inputs = DefaultedList.ofSize(ingredients.size(), Blocks.AIR);

            for (int i = 0; i < inputs.size(); ++i) {
                //inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new SingleSpell(identifier, output, inputs);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, SingleSpell recipe) {
            packetByteBuf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(packetByteBuf);
            }
            packetByteBuf.writeItemStack(recipe.getOutput());
        }

        @Nullable
        @Override
        public SingleSpell read(Identifier identifier, PacketByteBuf packetByteBuf) {
            DefaultedList<Block> inputs = DefaultedList.ofSize(packetByteBuf.readInt(), Blocks.AIR);

            for (int i = 0; i < inputs.size(); i++) {
                //inputs.set(i, Ingredient.fromPacket(packetByteBuf));
            }

            ItemStack output = packetByteBuf.readItemStack();

            return new SingleSpell(identifier, output, inputs);
        }
    }
}
