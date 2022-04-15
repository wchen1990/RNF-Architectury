package com.rocketnotfound.rnf.data.spells;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.rocketnotfound.rnf.block.RNFBlocks;
import com.rocketnotfound.rnf.util.RecipeHelper;
import dev.architectury.core.AbstractRecipeSerializer;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class SingleSpell implements ISpell {
    public static final Identifier TYPE = createIdentifier("single_spell");

    protected final Identifier id;
    protected final ItemStack output;
    protected final List<Pair<String, BlockStateArgument>> initialState;
    protected final List<Pair<String, BlockState>> finalState;

    public SingleSpell(Identifier id, ItemStack output, List<Pair<String, BlockStateArgument>> initialState, List<Pair<String, BlockState>> finalState) {
        this.id = id;
        this.output = output;
        this.initialState = initialState;
        this.finalState = finalState;
    }

    @Override
    public Spell getSpellType() {
        return Spell.SINGLE;
    }

    @Override
    public List<Pair<String, BlockStateArgument>> getInitialState() {
        return this.initialState;
    }

    public List<Pair<String, BlockState>> getFinalState() {
        return this.finalState;
    }

    @Override
    public int getLength() {
        return this.initialState.size();
    }

    @Override
    public boolean matches(List<BlockPos> positions, ServerWorld world) {
        List<BlockPos> posCopy = new ArrayList<>(positions);
        if (initialState.size() > 0 && posCopy.size() > 1 && initialState.size() < posCopy.size()) {
            if(world.getBlockState(posCopy.get(posCopy.size() - 1)).isOf(RNFBlocks.RITUAL_TRANSCRIBER.get())) {
                Collections.reverse(posCopy);
            }

            boolean matches = true;
            for (int idx = 0; idx < initialState.size(); ++idx) {
                BlockStateArgument bsa = initialState.get(idx).getRight();
                matches = matches && bsa.test(world, posCopy.get(idx + 1));
                if (!matches) break;
            }

            return matches;
        }
        return false;
    }

    @Override
    public void cast(List<BlockPos> positions, ServerWorld world) {
        if (matches(positions, world) && finalState.size() > 0) {
            List<BlockPos> posCopy = new ArrayList<>(positions);
            if(world.getBlockState(posCopy.get(posCopy.size() - 1)).isOf(RNFBlocks.RITUAL_TRANSCRIBER.get())) {
                Collections.reverse(posCopy);
            }

            for (int idx = 0; idx < finalState.size(); ++idx) {
                world.setBlockState(posCopy.get(idx + 1), finalState.get(idx).getRight());
            }
        }
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
        return RNFSpells.SINGLE_SPELL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RNFSpells.SINGLE_SPELL_TYPE.get();
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

            return new SingleSpell(identifier, output, inputs, outputs);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, SingleSpell recipe) {
            packetByteBuf.writeInt(recipe.getLength());
            for (Pair<String, BlockStateArgument> pair : recipe.getInitialState()) {
                packetByteBuf.writeString(pair.getLeft());
            }
            for (Pair<String, BlockState> pair : recipe.getFinalState()) {
                packetByteBuf.writeString(pair.getLeft());
            }
            packetByteBuf.writeItemStack(recipe.getOutput());
        }

        @Nullable
        @Override
        public SingleSpell read(Identifier identifier, PacketByteBuf packetByteBuf) {
            BlockStateArgumentType bsat = BlockStateArgumentType.blockState();

            List<Pair<String, BlockStateArgument>> inputs = new ArrayList<>();
            try {
                for (int i = 0; i < inputs.size(); ++i) {
                    String req = packetByteBuf.readString();
                    inputs.add(new Pair(req, bsat.parse(new StringReader(req))));
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }

            List<Pair<String, BlockState>> outputs = new ArrayList<>();
            try {
                for (int i = 0; i < outputs.size(); ++i) {
                    String req = packetByteBuf.readString();
                    outputs.add(new Pair(req, bsat.parse(new StringReader(req)).getBlockState()));
                }
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }

            ItemStack output = packetByteBuf.readItemStack();

            return new SingleSpell(identifier, output, inputs, outputs);
        }
    }
}
