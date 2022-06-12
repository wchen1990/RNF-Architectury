package com.rocketnotfound.rnf.network;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.data.managers.RitualManager;
import com.rocketnotfound.rnf.data.managers.SpellManager;
import com.rocketnotfound.rnf.data.spells.ISpell;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SyncSpellManager {
    private final List<Recipe<?>> recipes;

    public SyncSpellManager(Collection<ISpell> collection) {
        this.recipes = Lists.newArrayList(collection);
    }

    public SyncSpellManager(PacketByteBuf arg) {
        this.recipes = arg.readList(SyncSpellManager::readRecipe);
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public static void encode(SyncSpellManager message, PacketByteBuf buf) {
        buf.writeCollection(message.recipes, SyncSpellManager::writeRecipe);
    }

    public static SyncSpellManager decode(PacketByteBuf buf) {
        return new SyncSpellManager(buf);
    }

    public static void handle(SyncSpellManager message, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            if (context.getEnv() == EnvType.CLIENT) {
                SpellManager.getInstance().setSpells(message.getRecipes().stream().map((r) -> (ISpell)r).collect(Collectors.toList()));
            }
        });
    }

    public static Recipe<?> readRecipe(PacketByteBuf arg) {
        Identifier identifier = arg.readIdentifier();
        Identifier identifier2 = arg.readIdentifier();
        return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOrEmpty(identifier).orElseThrow(() -> {
            return new IllegalArgumentException("Unknown recipe serializer " + identifier);
        })).read(identifier2, arg);
    }

    public static <T extends Recipe> void writeRecipe(PacketByteBuf arg, T arg2) {
        arg.writeIdentifier(Registry.RECIPE_SERIALIZER.getId(arg2.getSerializer()));
        arg.writeIdentifier(arg2.getId());
        arg2.getSerializer().write(arg, arg2);
    }
}
