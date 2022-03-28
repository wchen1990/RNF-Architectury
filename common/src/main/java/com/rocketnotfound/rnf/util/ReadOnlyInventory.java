package com.rocketnotfound.rnf.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ReadOnlyInventory implements Inventory {
    private List<ItemStack> inventory;

    public static ReadOnlyInventory of(List<ItemStack> list) {
        return new ReadOnlyInventory(list);
    }

    protected ReadOnlyInventory(List<ItemStack> list) {
        inventory = list;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int i) {
        return inventory.get(i);
    }

    @Override
    public ItemStack removeStack(int i, int j) {
        return null;
    }

    @Override
    public ItemStack removeStack(int i) {
        return null;
    }

    @Override
    public void setStack(int i, ItemStack itemStack) {}

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity playerEntity) {
        return false;
    }

    @Override
    public void clear() {}
}
