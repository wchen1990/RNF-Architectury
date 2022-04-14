package com.rocketnotfound.rnf.data.spells;

import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

public interface ISpellType<T extends ISpell> extends RecipeType {
    default Optional<T> match(T spell, ServerWorld world, List<BlockPos> positions) {
        return spell.matches(positions, world) ? Optional.of(spell) : Optional.empty();
    }
}
