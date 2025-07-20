package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@MixinEnvironment(value = "client", type = MixinEnvironment.Env.MAIN)
@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {
    @Shadow public abstract Holder<TrimMaterial> material();

    @ModifyArg(
            method = "addToTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/MutableComponent;append(Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    private Component correctMaterialColour(Component original, @Local(argsOnly = true) DataComponentGetter componentGetter) {
        TrimPalette palette = TrimicaClient.getPalettes().getPalette(material().value(), null, componentGetter);
        if (palette == null) return original;

        if(palette.isAnimated()) {
            return original.copy().withColor(palette.getTooltipColour());
        } else {
            TextColor color = original.getStyle().getColor();
            if(color == null) {
                return original.copy().withColor(palette.getTooltipColour());
            }
        }
        return original;
    }

    @Inject(
            method = "addToTooltip",
            at = @At("RETURN")
    )
    private void addTrimMaterialAdditionLine(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter, CallbackInfo ci) {
        MaterialAdditions additions = dataComponentGetter.get(MaterialAdditions.TYPE);
        if (additions != null) {
            List<Component> additionsList = new ArrayList<>();
            for (ResourceLocation addition : additions.additionKeys()) {
                Item additionItem = BuiltInRegistries.ITEM.getValue(addition);
                if (additionItem != Items.AIR) {
                    additionsList.add(CommonComponents.space().append(additionItem.getName()).withStyle(ChatFormatting.AQUA));
                }
            }
            if (!additionsList.isEmpty()) {
                consumer.accept(Component.translatable("trimica.material_addition_list").withStyle(ChatFormatting.GRAY));
                for (Component addition : additionsList) {
                    consumer.accept(addition);
                }
            }
        }
    }
}
