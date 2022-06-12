package com.rocketnotfound.rnf.network;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import com.rocketnotfound.rnf.RNF;
import com.rocketnotfound.rnf.data.managers.RitualManager;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SyncRitualManager {
    private final List<Recipe<?>> recipes;

    public SyncRitualManager(Collection<Recipe<?>> collection) {
        this.recipes = Lists.newArrayList(collection);
    }

    public SyncRitualManager(PacketByteBuf arg) {
        this.recipes = arg.readList(SyncRitualManager::readRecipe);
    }

    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public static void encode(SyncRitualManager message, PacketByteBuf buf) {
        buf.writeCollection(message.recipes, SyncRitualManager::writeRecipe);
    }

    public static SyncRitualManager decode(PacketByteBuf buf) {
        return new SyncRitualManager(buf);
    }

    public static void handle(SyncRitualManager message, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            if (context.getEnv() == EnvType.CLIENT) {
                RitualManager.getInstance().setRecipes(message.getRecipes());
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
