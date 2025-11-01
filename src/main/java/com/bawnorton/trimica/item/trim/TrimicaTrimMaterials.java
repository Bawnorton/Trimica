package com.bawnorton.trimica.item.trim;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class TrimicaTrimMaterials {
	public static final ResourceKey<TrimMaterial> RAINBOW = registerKey("rainbow");

	public static void bootstrap(BootstrapContext<TrimMaterial> context) {
		register(
				context,
				RAINBOW,
				Style.EMPTY.withColor(ChatFormatting.WHITE),
				MaterialAssetGroup.create("rainbow"),
				new MaterialAdditions(BuiltInRegistries.ITEM.getKey(TrimicaItems.ANIMATOR))
		);
	}

	private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> key, Style style, MaterialAssetGroup assets, MaterialAdditions additions) {
		Component component = Component.translatable(Util.makeDescriptionId(
				"trim_material",
				Trimica.rl(key.location().getPath())
		)).withStyle(style);
		TrimMaterial material = new TrimMaterial(assets, component);
		Trimica.getMaterialRegistry().setIntrinsicAdditions(material, additions);
		context.register(key, material);
	}

	private static ResourceKey<TrimMaterial> registerKey(String name) {
		return ResourceKey.create(Registries.TRIM_MATERIAL, Trimica.rl(name));
	}
}
