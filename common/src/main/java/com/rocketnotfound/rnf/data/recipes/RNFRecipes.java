package com.rocketnotfound.rnf.data.recipes;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class RNFRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(RNF.MOD_ID, Registry.RECIPE_SERIALIZER_KEY);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(RNF.MOD_ID, Registry.RECIPE_TYPE_KEY);

    public static final RegistrySupplier<RecipeType> RITUAL_TYPE = RECIPE_TYPES.register(RitualRecipe.TYPE, RitualRecipe.RitualRecipeType::new);
    public static final RegistrySupplier<RecipeSerializer> RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(RitualRecipe.TYPE, RitualRecipe.Serializer::new);

    public static void register() {
        RECIPE_TYPES.register();
        RECIPE_SERIALIZERS.register();
    }
}