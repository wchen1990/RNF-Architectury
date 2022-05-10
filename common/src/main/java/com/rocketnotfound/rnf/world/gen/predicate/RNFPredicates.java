package com.rocketnotfound.rnf.world.gen.predicate;

import com.rocketnotfound.rnf.RNF;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.structure.rule.PosRuleTestType;
import net.minecraft.util.registry.Registry;

public class RNFPredicates {
    public static final DeferredRegister<PosRuleTestType<?>> POSITION_PREDICATES = DeferredRegister.create(RNF.MOD_ID, Registry.POS_RULE_TEST_KEY);

    public static final RegistrySupplier<PosRuleTestType<?>> HORIZONTAL_DISTANCE_AWAY_FROM_ORIGIN = POSITION_PREDICATES.register("horizontal_distance_away_from_origin", () -> HorizontalDistanceAwayFromOrigin.TYPE);
}
