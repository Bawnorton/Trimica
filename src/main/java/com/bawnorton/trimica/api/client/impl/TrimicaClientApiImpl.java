package com.bawnorton.trimica.api.client.impl;

import com.bawnorton.trimica.api.BaseTextureInterceptor;
import com.bawnorton.trimica.api.PaletteInterceptor;
import com.bawnorton.trimica.api.client.TrimicaClientApi;
import com.bawnorton.trimica.api.client.TrimicaRenderer;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.bawnorton.trimica.util.SortableEndpointHolder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.ApiStatus;

import java.util.PriorityQueue;
import java.util.Queue;

@ApiStatus.Internal
public final class TrimicaClientApiImpl implements TrimicaClientApi {
	public static final TrimicaClientApiImpl INSTANCE = new TrimicaClientApiImpl();

	private final TrimicaRenderer renderer = new TrimicaRendererImpl();

	private final Queue<SortableEndpointHolder<BaseTextureInterceptor>> baseTextureInterceptors = new PriorityQueue<>();
	private final Queue<SortableEndpointHolder<PaletteInterceptor>> paletteInterceptors = new PriorityQueue<>();

	public TrimicaRenderer getRenderer() {
		return renderer;
	}

	public void registerBaseTextureInterceptor(int priority, BaseTextureInterceptor baseTextureInterceptor) {
		baseTextureInterceptors.add(new SortableEndpointHolder<>(baseTextureInterceptor, priority));
	}

	public void registerPaletteInterceptor(int priority, PaletteInterceptor paletteInterceptor) {
		paletteInterceptors.add(new SortableEndpointHolder<>(paletteInterceptor, priority));
	}

	public ResourceLocation applyBaseTextureInterceptorsForItem(ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
		for (SortableEndpointHolder<BaseTextureInterceptor> endpointHolder : baseTextureInterceptors) {
			expectedBaseTexture = endpointHolder.endpoint().interceptItemTexture(expectedBaseTexture, itemWithTrim, armourTrim);
		}
		return expectedBaseTexture;
	}

	public ResourceLocation applyBaseTextureInterceptorsForArmour(ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
		for (SortableEndpointHolder<BaseTextureInterceptor> endpointHolder : baseTextureInterceptors) {
			expectedBaseTexture = endpointHolder.endpoint().interceptArmourTexture(expectedBaseTexture, itemWithTrim, armourTrim);
		}
		return expectedBaseTexture;
	}

	public ResourceLocation applyBaseTextureInterceptorsForShield(ResourceLocation expectedBaseTexture, DataComponentGetter componentGetter, ArmorTrim armourTrim) {
		for (SortableEndpointHolder<BaseTextureInterceptor> endpointHolder : baseTextureInterceptors) {
			expectedBaseTexture = endpointHolder.endpoint().interceptShieldTexture(expectedBaseTexture, componentGetter, armourTrim);
		}
		return expectedBaseTexture;
	}

	public TrimPalette applyPaletteInterceptorsForGeneration(TrimPalette generated, TrimMaterial material, MaterialAdditions materialAdditions) {
		for (SortableEndpointHolder<PaletteInterceptor> endpointHolder : paletteInterceptors) {
			generated = endpointHolder.endpoint().interceptPaletteGeneration(generated, material, materialAdditions);
		}
		return generated;
	}
}
