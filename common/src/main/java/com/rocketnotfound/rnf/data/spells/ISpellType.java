package com.rocketnotfound.rnf.data.spells;

import net.minecraft.block.Block;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public interface ISpellType<T extends ISpell> extends RecipeType {
    default Optional<T> match(T spell, World world, List<Block> blocks) {
        return spell.matches(blocks, world) ? Optional.of(spell) : Optional.empty();
    }
}
