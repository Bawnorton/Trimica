package com.bawnorton.trimica.mixin.crafting;

import com.bawnorton.trimica.item.component.AdditionalTrims;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
	@Shadow
	private int repairItemCountCost;

	@Shadow
	private @Nullable String itemName;

	@Shadow
	@Final
	private DataSlot cost;

	public AnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, ItemCombinerMenuSlotDefinition slotDefinition) {
		super(menuType, containerId, inventory, access, slotDefinition);
	}

	@Inject(
			method = "createResult",
			at = @At("HEAD"),
			cancellable = true
	)
	private void addTrimRemovalResult(CallbackInfo ci) {
		ItemStack armour = inputSlots.getItem(0);
		ItemStack tool = inputSlots.getItem(1);

		ItemStack mutableArmour = armour.copy();
		if (tool.is(Items.FLINT)) {
			AdditionalTrims.correctTrimComponents(mutableArmour);
			int remainder = AdditionalTrims.removeTrims(mutableArmour, tool.getCount());
			trimica$applyTool(ci, armour, tool, mutableArmour, remainder);
		} else if (tool.is(Items.FEATHER) && MaterialAdditions.enableMaterialAdditions) {
			int remainder = MaterialAdditions.removeMaterials(mutableArmour, tool.getCount());
			trimica$applyTool(ci, armour, tool, mutableArmour, remainder);
		}
	}

	@Unique
	private void trimica$applyTool(CallbackInfo ci, ItemStack armour, ItemStack tool, ItemStack mutableArmour, int remainder) {
		if (!ItemStack.matches(mutableArmour, armour)) {
			resultSlots.setItem(0, mutableArmour);
			repairItemCountCost = tool.getCount() - remainder;

			int cost = repairItemCountCost;
			if (itemName != null && !StringUtil.isBlank(itemName)) {
				if (!itemName.equals(armour.getHoverName().getString())) {
					cost += 1;
					mutableArmour.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
				}
			} else if (armour.has(DataComponents.CUSTOM_NAME)) {
				cost += 1;
				mutableArmour.remove(DataComponents.CUSTOM_NAME);
			}
			this.cost.set(cost);

			broadcastChanges();
			ci.cancel();
		}
	}
}
