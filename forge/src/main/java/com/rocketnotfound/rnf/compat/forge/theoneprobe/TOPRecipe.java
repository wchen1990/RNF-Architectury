package com.rocketnotfound.rnf.compat.forge.theoneprobe;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class TOPRecipe extends SpecialCraftingRecipe {
    public static final SpecialRecipeSerializer<TOPRecipe> SERIALIZER = new SpecialRecipeSerializer<>(TOPRecipe::new);

    public TOPRecipe(Identifier id) {
        super(id);
    }

    protected boolean stackMeetsCriteria(ItemStack stack) {
        return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.HEAD;
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
        boolean foundSource = false;
        boolean foundTarget = false;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (item.equals(ForgeRegistries.ITEMS.getValue(new Identifier("theoneprobe:probe")))) {
                    if (foundTarget) {
                        return false;
                    }
                    foundTarget = true;
                } else if (stackMeetsCriteria(stack)) {
                    if (foundSource) {
                        return false;
                    }
                    foundSource = true;
                } else {
                    return false;
                }
            }
        }

        return foundSource && foundTarget;
    }

    @Nonnull
    @Override
    public ItemStack craft(@Nonnull CraftingInventory inv) {
        ItemStack target = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); i++) {
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                if (stackMeetsCriteria(stack)) {
                    target = stack;
                }
            }
        }

        ItemStack copy = target.copy();
        copy.setSubNbt("theoneprobe", NbtInt.of(1));
        NbtCompound nbt = copy.getNbt();
        if (nbt != null) {
            NbtCompound displayNBT = nbt.contains("display") ? (NbtCompound) nbt.get("display") : new NbtCompound();
            NbtList listNBT = displayNBT.contains("Lore") ? (NbtList) displayNBT.get("Lore") : new NbtList();
            listNBT.add(NbtString.of("\"Probe Attached\""));
            displayNBT.put("Lore", listNBT);
            nbt.put("display", displayNBT);
            copy.setNbt(nbt);
        }

        return copy;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }
}
