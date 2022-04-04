package com.rocketnotfound.rnf.data.recipes;

import com.rocketnotfound.rnf.item.RNFItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

import static com.rocketnotfound.rnf.RNF.createIdentifier;

public class RuneEngravementRecipe implements IRitualRecipe {
    public static final Identifier TYPE = createIdentifier("rune_engravement");

    protected final Inventory recipeItems;
    protected final Random random;

    public RuneEngravementRecipe(Inventory recipeItems) {
        this.recipeItems = recipeItems;
        this.random = new Random();
    }

    public static boolean isValid(Inventory inventory, World world) {
        return inventory.size() == 4
            && inventory.getStack(0).isOf(RNFItems.RUNE_BLOCK.get())
            && inventory.getStack(1).isOf(Items.AMETHYST_SHARD)
            && inventory.getStack(2).isOf(Items.BLAZE_ROD)
            && !inventory.getStack(3).isEmpty();
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return RuneEngravementRecipe.isValid(inventory, world);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        if (matches(inventory, null)) {
            Identifier id = Registry.ITEM.getId(inventory.getStack(3).getItem());
            if (id != null) {
                String validLetters = id.getPath().toLowerCase().replaceAll("[^a-z]", "");
                return Registry.ITEM.get(
                    createIdentifier(
                        String.format(
                            "rune_block_%s",
                            validLetters.charAt(random.nextInt(validLetters.length()))
                        )
                    )
                ).getDefaultStack();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return this.craft(this.recipeItems);
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }
}
