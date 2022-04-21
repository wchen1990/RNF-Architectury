package com.rocketnotfound.rnf.data.spells;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.rocketnotfound.rnf.data.spells.SpellEffects.SpellEffectDeserialize;
import com.rocketnotfound.rnf.util.RecipeHelper;
import dev.architectury.core.AbstractRecipeSerializer;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;

import javax.annotation.Nullable;
import java.util.*;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class NormalSpell implements ISpell {
    public static final Identifier TYPE = createIdentifier("normal_spell");

    protected final Identifier id;
    protected final ItemStack output;
    protected final List<Pair<String, BlockStateArgument>> initialState;
    protected final List<Pair<String, BlockState>> finalState;
    protected final List<Pair<String, Optional<NbtCompound>>> effects;

    public NormalSpell(Identifier id, ItemStack output, List<Pair<String, BlockStateArgument>> initialState, List<Pair<String, BlockState>> finalState, List<Pair<String, Optional<NbtCompound>>> effects) {
        this.id = id;
        this.output = output;
        this.initialState = initialState;
        this.finalState = finalState;
        this.effects = effects;
    }

    @Override
    public Spell getSpellType() {
        return Spell.NORMAL;
    }

    @Override
    public List<Pair<String, BlockStateArgument>> getInitialState() {
        return this.initialState;
    }

    @Override
    public List<Pair<String, BlockState>> getFinalState() {
        return this.finalState;
    }

    @Override
    public int getLength() {
        return this.initialState.size();
    }

    @Override
    public List<Pair<String, Optional<NbtCompound>>> getEffects() {
        return effects;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return getOutput();
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
        return RNFSpells.NORMAL_SPELL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFSpells.NORMAL_SPELL_TYPE.get();
    }

    public static class SpellType implements ISpellType<NormalSpell> {
        @Override
        public String toString() {
            return TYPE.toString();
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<NormalSpell> {
        @Override
        public NormalSpell read(Identifier identifier, JsonObject jsonObject) {
            BlockStateArgumentType bsat = BlockStateArgumentType.blockState();
            Map<String, Pair<String, BlockStateArgument>> map = RecipeHelper.readSymbols(JsonHelper.getObject(jsonObject, "key"), (jsonElement) -> {
                if (jsonElement.isJsonPrimitive()) {
                    try {
                        String key = jsonElement.getAsString();
                        return new Pair(key, bsat.parse(new StringReader(key)));
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }, null);

            ItemStack output = (JsonHelper.hasJsonObject(jsonObject,"output")) ?
                    ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "output")) : ItemStack.EMPTY;

            JsonObject pattern = JsonHelper.getObject(jsonObject, "pattern");

            List<Pair<String, BlockStateArgument>> inputs = new ArrayList<>();
            String initial = JsonHelper.getString(pattern, "initial");
            for (int i = 0; i < initial.length(); ++i) {
                String req = String.valueOf(initial.charAt(i));
                inputs.add(new Pair<String, BlockStateArgument>(map.get(req).getLeft(), map.get(req).getRight()));
            }

            List<Pair<String, BlockState>> outputs = new ArrayList<>();
            if (JsonHelper.hasString(pattern,"after")) {
                String after = JsonHelper.getString(pattern, "after");
                for (int i = 0; i < after.length(); ++i) {
                    String req = String.valueOf(after.charAt(i));
                    outputs.add(new Pair<String, BlockState>(map.get(req).getLeft(), map.get(req).getRight().getBlockState()));
                }
            }

            List<Pair<String, Optional<NbtCompound>>> effects = new ArrayList<>();
            if (JsonHelper.hasArray(jsonObject, "effects")) {
                JsonArray effectsObj = JsonHelper.getArray(jsonObject, "effects");
                for (JsonElement effect : effectsObj) {
                    if (effect.isJsonObject()) {
                        JsonObject effectObj = effect.getAsJsonObject();
                        String type = JsonHelper.getString(effectObj, "type");

                        SpellEffectDeserialize spellDeserialize = SpellEffects.TYPE_MAP.getOrDefault(type, null);
                        if (spellDeserialize != null) {
                            NbtCompound variables = null;
                            if (JsonHelper.hasJsonObject(effectObj, "variables")) {
                                try {
                                    variables = StringNbtReader.parse(JsonHelper.getObject(effectObj, "variables").toString());
                                } catch (CommandSyntaxException e) {
                                    e.printStackTrace();
                                }
                            }
                            effects.add(new Pair<String, Optional<NbtCompound>>(type, Optional.ofNullable(variables)));
                        }
                    }
                }
            }

            return new NormalSpell(identifier, output, inputs, outputs, effects);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, NormalSpell recipe) {
            packetByteBuf.writeInt(recipe.getLength());
            for (Pair<String, BlockStateArgument> pair : recipe.getInitialState()) {
                packetByteBuf.writeString(pair.getLeft());
            }

            packetByteBuf.writeInt(recipe.getFinalState() != null ? recipe.getFinalState().size() : 0);
            for (Pair<String, BlockState> pair : recipe.getFinalState()) {
                packetByteBuf.writeString(pair.getLeft());
            }

            packetByteBuf.writeInt(recipe.getEffects() != null ? recipe.getEffects().size() : 0);
            for (Pair<String, Optional<NbtCompound>> pair : recipe.getEffects()) {
                packetByteBuf.writeString(pair.getLeft());
                packetByteBuf.writeNbt(pair.getRight().isPresent() ? pair.getRight().get() : null);
            }

            packetByteBuf.writeItemStack(recipe.getOutput());
        }

        @Nullable
        @Override
        public NormalSpell read(Identifier identifier, PacketByteBuf packetByteBuf) {
            BlockStateArgumentType bsat = BlockStateArgumentType.blockState();

            int inputSize = packetByteBuf.readInt();
            List<Pair<String, BlockStateArgument>> inputs = new ArrayList<>();
            try {
                for (int i = 0; i < inputSize; ++i) {
                    String req = packetByteBuf.readString();
                    inputs.add(new Pair(req, bsat.parse(new StringReader(req))));
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }

            int outputSize = packetByteBuf.readInt();
            List<Pair<String, BlockState>> outputs = new ArrayList<>();
            try {
                for (int i = 0; i < outputSize; ++i) {
                    String req = packetByteBuf.readString();
                    outputs.add(new Pair(req, bsat.parse(new StringReader(req)).getBlockState()));
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }

            int effectSize = packetByteBuf.readInt();
            List<Pair<String, Optional<NbtCompound>>> effects = new ArrayList<>();
            for (int i = 0; i < effectSize; ++i) {
                String type = packetByteBuf.readString();
                effects.add(new Pair<String, Optional<NbtCompound>>(type, Optional.ofNullable(packetByteBuf.readNbt())));
            }

            ItemStack output = packetByteBuf.readItemStack();

            return new NormalSpell(identifier, output, inputs, outputs, effects);
        }
    }
}
