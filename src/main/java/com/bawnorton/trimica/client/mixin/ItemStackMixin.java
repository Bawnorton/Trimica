package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.tags.TrimicaTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(value = ItemStack.class, priority = 1500)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean is(TagKey<Item> tag);

    @ModifyReturnValue(
            method = "getTooltipLines",
            at = @At("RETURN:LAST")
    )
    private List<Component> addTrimMaterialAdditionLine(List<Component> original) {
        if(is(TrimicaTags.MATERIAL_ADDITIONS)) {
            original.add(Component.translatable("trimica.material_addition").withStyle(ChatFormatting.GOLD));
        }
        if(is(ItemTags.TRIM_MATERIALS)) {
            original.add(Component.translatable("trimica.trim_material").withStyle(ChatFormatting.GREEN));
        }
        return original;
    }
}
