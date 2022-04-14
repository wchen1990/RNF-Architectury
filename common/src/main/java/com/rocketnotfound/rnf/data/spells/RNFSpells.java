package com.rocketnotfound.rnf.data.spells;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class RNFSpells {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(RNF.MOD_ID, Registry.RECIPE_SERIALIZER_KEY);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(RNF.MOD_ID, Registry.RECIPE_TYPE_KEY);

    public static final RegistrySupplier<ISpellType> SINGLE_SPELL_TYPE = RECIPE_TYPES.register(SingleSpell.TYPE, SingleSpell.SpellType::new);
    public static final RegistrySupplier<RecipeSerializer> SINGLE_SPELL_SERIALIZER = RECIPE_SERIALIZERS.register(SingleSpell.TYPE, SingleSpell.Serializer::new);

    public static void register() {
        RECIPE_TYPES.register();
        RECIPE_SERIALIZERS.register();
    }
}
