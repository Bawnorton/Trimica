package com.bawnorton.trimica.crafting;

import com.bawnorton.trimica.api.CraftingRecipeInterceptor;
import com.bawnorton.trimica.platform.Platform;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
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
        if (item instanceof StandingAndWallBlockItem || item == Items.CARVED_PUMPKIN) {
            return TriState.FALSE;
        }
        if (!Platform.isModLoaded("elytratrims") && item == Items.ELYTRA) {
            return TriState.FALSE;
        }
        EquipmentSlot slot = equippable.slot();
        if (slot == EquipmentSlot.HEAD || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET || item == Items.SHIELD) {
            return TriState.TRUE;
        }
        return TriState.DEFAULT;
    }
}
