package com.bawnorton.trimica.mixin.crafting;

import com.bawnorton.trimica.crafting.MaterialAdditionRecipe;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@MixinEnvironment
@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {
    @Shadow @Final private Level level;

    private SmithingMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition) {
        super(menuType, i, inventory, containerLevelAccess, itemCombinerMenuSlotDefinition);
    }

    @Shadow protected abstract SmithingRecipeInput createRecipeInput();

    @WrapOperation(
            method = "slotsChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V"
            )
    )
    private void orAdditionRecipeHasError(DataSlot instance, int i, Operation<Void> original) {
        if(i == 0) {
            if (level instanceof ServerLevel serverLevel) {
                Optional<RecipeHolder<SmithingRecipe>> optional = serverLevel.recipeAccess()
                                                                             .getRecipeFor(RecipeType.SMITHING, createRecipeInput(), serverLevel);
                if(optional.map(holder -> holder.value() instanceof MaterialAdditionRecipe).orElse(false)) {
                    if(getSlot(1).hasItem() && getSlot(2).hasItem() && !getSlot(this.getResultSlot()).hasItem()) {
                        i = 1;
                    }
                }
            }
        }
        original.call(instance, i);
    }
}
