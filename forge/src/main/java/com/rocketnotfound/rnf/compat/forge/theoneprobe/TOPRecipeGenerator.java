package com.rocketnotfound.rnf.compat.forge.theoneprobe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import java.util.function.Consumer;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class TOPRecipeGenerator extends RecipeProvider implements IConditionBuilder {
    public TOPRecipeGenerator(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void generate(Consumer<RecipeJsonProvider> consumer) {
        specialRecipe(TOPRecipe.SERIALIZER, consumer);
    }

    @Override
    public String getName() {
        return "RNF TOP Compat Crafting Recipes";
    }

    private static void specialRecipe(SpecialRecipeSerializer<?> serializer, Consumer<RecipeJsonProvider> consumer) {
        ComplexRecipeJsonBuilder.create(serializer).offerTo(consumer, createIdentifier("dynamic/" + serializer.getRegistryName().getPath()).toString());
    }
}
