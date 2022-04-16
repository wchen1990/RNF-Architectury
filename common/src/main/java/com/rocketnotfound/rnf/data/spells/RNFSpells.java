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

    public static final RegistrySupplier<ISpellType> NORMAL_SPELL_TYPE = RECIPE_TYPES.register(NormalSpell.TYPE, NormalSpell.SpellType::new);
    public static final RegistrySupplier<RecipeSerializer> NORMAL_SPELL_SERIALIZER = RECIPE_SERIALIZERS.register(NormalSpell.TYPE, NormalSpell.Serializer::new);

    public static final RegistrySupplier<ISpellType> PRIMING_SPELL_TYPE = RECIPE_TYPES.register(PrimingSpell.TYPE, PrimingSpell.SpellType::new);
    public static final RegistrySupplier<RecipeSerializer> PRIMING_SPELL_SERIALIZER = RECIPE_SERIALIZERS.register(PrimingSpell.TYPE, PrimingSpell.Serializer::new);

    public static void register() {
        RECIPE_TYPES.register();
        RECIPE_SERIALIZERS.register();
    }
}
