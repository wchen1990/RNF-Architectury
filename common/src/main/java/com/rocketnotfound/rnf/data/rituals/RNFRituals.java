package com.rocketnotfound.rnf.data.rituals;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class RNFRituals {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(RNF.MOD_ID, Registry.RECIPE_SERIALIZER_KEY);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(RNF.MOD_ID, Registry.RECIPE_TYPE_KEY);

    public static final RegistrySupplier<RecipeType> RITUAL_TYPE = RECIPE_TYPES.register(NormalRitual.TYPE, NormalRitual.RitualType::new);
    public static final RegistrySupplier<RecipeSerializer> RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(NormalRitual.TYPE, NormalRitual.Serializer::new);

    public static final RegistrySupplier<RecipeType> CIRCLE_RITUAL_TYPE = RECIPE_TYPES.register(CircleRitual.TYPE, CircleRitual.RitualType::new);
    public static final RegistrySupplier<RecipeSerializer> CIRCLE_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(CircleRitual.TYPE, CircleRitual.Serializer::new);

    public static final RegistrySupplier<RecipeType> RUNE_ENGRAVEMENT_TYPE = RECIPE_TYPES.register(RuneEngravement.TYPE, RuneEngravement.RitualType::new);
    public static final RegistrySupplier<RecipeSerializer> RUNE_ENGRAVEMENT_SERIALIZER = RECIPE_SERIALIZERS.register(RuneEngravement.TYPE, RuneEngravement.Serializer::new);

    public static final RegistrySupplier<RecipeType> TETHER_RITUAL_TYPE = RECIPE_TYPES.register(TetheredRitual.TYPE, TetheredRitual.RitualType::new);
    public static final RegistrySupplier<RecipeSerializer> TETHER_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(TetheredRitual.TYPE, TetheredRitual.Serializer::new);

    public static final RegistrySupplier<RecipeType> ANCHOR_RITUAL_TYPE = RECIPE_TYPES.register(AnchorRitual.TYPE, AnchorRitual.RitualType::new);
    public static final RegistrySupplier<RecipeSerializer> ANCHOR_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(AnchorRitual.TYPE, AnchorRitual.Serializer::new);

    public static final RegistrySupplier<RecipeType> INFUSION_RITUAL_TYPE = RECIPE_TYPES.register(InfusionRitual.TYPE, InfusionRitual.RitualType::new);
    public static final RegistrySupplier<RecipeSerializer> INFUSION_RITUAL_SERIALIZER = RECIPE_SERIALIZERS.register(InfusionRitual.TYPE, InfusionRitual.Serializer::new);

    public static void register() {
        RECIPE_TYPES.register();
        RECIPE_SERIALIZERS.register();
    }
}
