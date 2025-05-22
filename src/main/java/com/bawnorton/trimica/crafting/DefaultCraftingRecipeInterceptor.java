package com.bawnorton.trimica.crafting;

import com.bawnorton.trimica.api.CraftingRecipeInterceptor;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

public class DefaultCraftingRecipeInterceptor implements CraftingRecipeInterceptor {
    @Override
    public TriState allowAsAddition(Item item) {
        if(item == Items.AIR) {
            return TriState.FALSE;
        }
        return TriState.DEFAULT;
    }

    @Override
    public TriState allowAsBase(Item item) {
        DataComponentMap components = item.components();
        Equippable equippable = components.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return TriState.FALSE;
        }
        EquipmentSlot slot = equippable.slot();
        if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) {
            return TriState.TRUE;
        }
        if (item == Items.SHIELD) {
            return TriState.TRUE;
        }
        return TriState.DEFAULT;
    }
}
