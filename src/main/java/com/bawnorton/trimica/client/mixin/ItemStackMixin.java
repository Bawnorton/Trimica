package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.bawnorton.trimica.tags.TrimicaTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@MixinEnvironment(value = "client", type = MixinEnvironment.Env.MAIN)
@Mixin(value = ItemStack.class, priority = 1500)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Shadow public abstract boolean is(TagKey<Item> tag);

    @ModifyReturnValue(
            method = "getTooltipLines",
            at = @At("RETURN:LAST")
    )
    private List<Component> addTrimMaterialAdditionLine(List<Component> original) {
        if(is(TrimicaTags.MATERIAL_ADDITIONS) && MaterialAdditions.enableMaterialAdditions) {
            if (Screen.hasShiftDown()) {
                original.add(Component.translatable("trimica.material_addition.shift").withStyle(ChatFormatting.GOLD));
                original.add(CommonComponents.space().append(Component.translatable("trimica.material_addition.details.1").withStyle(ChatFormatting.GRAY)));
                original.add(CommonComponents.space().append(Component.translatable("trimica.material_addition.details.2").withStyle(ChatFormatting.RED)));
            } else {
                original.add(Component.translatable("trimica.material_addition.no_shift").withStyle(ChatFormatting.GOLD));
            }
        }
        if(is(ItemTags.TRIM_MATERIALS)) {
            original.add(Component.translatable("trimica.trim_material").withStyle(ChatFormatting.GREEN));
        }
        return original;
    }
}
