package com.rocketnotfound.rnf.compat.forge.curios;

import com.rocketnotfound.rnf.data.spells.SpellEffects;
import com.rocketnotfound.rnf.util.ItemEntityHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosCompat {
    public static final SpellEffects.FloatAffectedSpellRequires DISROBAL = (delay) -> (world, entity) -> {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack equipped = entity.getEquippedStack(slot);
            if (!equipped.isEmpty()) {
                entity.equipStack(slot, ItemStack.EMPTY);
                ItemEntityHelper.spawnItem(world, entity.getPos(), equipped, Vec3d.ZERO, (int) delay);
            }
        }

        CuriosApi.getCuriosHelper().getEquippedCurios(entity).ifPresent((handler) -> {
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack equippedCurio = handler.getStackInSlot(i);
                if (!equippedCurio.isEmpty()) {
                    handler.setStackInSlot(i, ItemStack.EMPTY);
                    ItemEntityHelper.spawnItem(world, entity.getPos(), equippedCurio, Vec3d.ZERO, (int) delay);
                }
            }
        });

        return entity;
    };

    public static void init() {
        SpellEffects.TYPE_MAP.put("disrobal", DISROBAL);
    }
}
