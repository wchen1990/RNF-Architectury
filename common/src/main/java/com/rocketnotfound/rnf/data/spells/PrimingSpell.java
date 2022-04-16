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

public class PrimingSpell extends NormalSpell {
    public static final Identifier TYPE = createIdentifier("priming_spell");

    protected final List<Pair<String, Optional<NbtCompound>>> effects;

    public PrimingSpell(Identifier id, ItemStack output, List<Pair<String, BlockStateArgument>> initialState, List<Pair<String, BlockState>> finalState, List<Pair<String, Optional<NbtCompound>>> effects) {
        super(id, output, initialState, finalState);
        this.effects = effects;
    }

    @Override
    public Spell getSpellType() {
        return Spell.PRIMING;
    }

    @Override
    public List<Pair<String, Optional<NbtCompound>>> getEffects() {
        return effects;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNFSpells.PRIMING_SPELL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFSpells.PRIMING_SPELL_TYPE.get();
    }

    public static class SpellType implements ISpellType<PrimingSpell> {
        @Override
        public String toString() {
            return TYPE.toString();
        }
    }
    public static class Serializer extends AbstractRecipeSerializer<PrimingSpell> {
        @Override
        public PrimingSpell read(Identifier identifier, JsonObject jsonObject) {
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

            return new PrimingSpell(identifier, output, inputs, outputs, effects);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, PrimingSpell recipe) {
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
        public PrimingSpell read(Identifier identifier, PacketByteBuf packetByteBuf) {
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

            return new PrimingSpell(identifier, output, inputs, outputs, effects);
        }
    }
}