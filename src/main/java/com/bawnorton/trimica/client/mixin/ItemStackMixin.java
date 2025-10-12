package com.bawnorton.trimica.client.mixin;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.item.component.AdditionalTrims;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.bawnorton.trimica.tags.TrimicaTags;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@MixinEnvironment(value = "client", type = MixinEnvironment.Env.MAIN)
@Mixin(value = ItemStack.class, priority = 1500)
public abstract class ItemStackMixin implements DataComponentHolder {
	@Shadow
	public abstract boolean is(TagKey<Item> tag);

	@Inject(
			method = "addDetailsToTooltip",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/core/component/DataComponents;TRIM:Lnet/minecraft/core/component/DataComponentType;",
					opcode = Opcodes.GETSTATIC
			)
	)
	private void addTrimicaAdditionLines(Item.TooltipContext context, TooltipDisplay tooltipDisplay, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> tooltipAdder, CallbackInfo ci) {
		if (AdditionalTrims.enableAdditionalTrims) {
			List<ArmorTrim> trims = AdditionalTrims.getAllTrims(this).reversed();
			if (!trims.isEmpty()) {
				tooltipAdder.accept(Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY));
			}
			for (ArmorTrim trim : trims) {
				TrimPattern pattern = trim.pattern().value();
				TrimMaterial material = trim.material().value();

				TrimPalette palette = TrimicaClient.getPalettes().getPalette(material, null, this);
				if (palette == null) palette = TrimPalette.DEFAULT;

				Style style = material.description().getStyle();
				if (style.getColor() == null || palette.isAnimated()) {
					style = style.withColor(palette.getTooltipColour());
				}
				Component patternComponent = CommonComponents.space().append(pattern.description().copy().withStyle(style));
				Component materialComponent = CommonComponents.space().append(material.description().copy().withStyle(style));
				tooltipAdder.accept(patternComponent);
				tooltipAdder.accept(materialComponent);
			}
		}
		if (MaterialAdditions.enableMaterialAdditions) {
			MaterialAdditions additions = get(MaterialAdditions.TYPE);
			if (additions != null) {
				List<Component> additionsList = new ArrayList<>();
				for (ResourceLocation addition : additions.additionKeys()) {
					Item additionItem = BuiltInRegistries.ITEM.getValue(addition);
					if (additionItem != Items.AIR) {
						additionsList.add(CommonComponents.space().append(additionItem.getName()).withStyle(ChatFormatting.AQUA));
					}
				}
				if (!additionsList.isEmpty()) {
					tooltipAdder.accept(Component.translatable("trimica.material_addition_list").withStyle(ChatFormatting.GRAY));
					for (Component addition : additionsList) {
						tooltipAdder.accept(addition);
					}
				}
			}

		}
		if (is(TrimicaTags.MATERIAL_ADDITIONS) && MaterialAdditions.enableMaterialAdditions) {
			//? if >=1.21.10 {
			if (Minecraft.getInstance().hasShiftDown()) {
			//?} else {
			/*if (Screen.hasShiftDown()) {
			*///?}
				tooltipAdder.accept(Component.translatable("trimica.material_addition.shift").withStyle(ChatFormatting.GOLD));
				tooltipAdder.accept(CommonComponents.space().append(Component.translatable("trimica.material_addition.details.1").withStyle(ChatFormatting.GRAY)));
				tooltipAdder.accept(CommonComponents.space().append(Component.translatable("trimica.material_addition.details.2").withStyle(ChatFormatting.RED)));
			} else {
				tooltipAdder.accept(Component.translatable("trimica.material_addition.no_shift").withStyle(ChatFormatting.GOLD));
			}
		}
		if (is(ItemTags.TRIM_MATERIALS)) {
			tooltipAdder.accept(Component.translatable("trimica.trim_material").withStyle(ChatFormatting.GREEN));
		}
	}
}
