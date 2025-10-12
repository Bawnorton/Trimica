package com.bawnorton.trimica.item.component;

import com.bawnorton.configurable.Configurable;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record MaterialAdditions(List<ResourceLocation> additionKeys) {
	/**
	 * Whether material additions are enabled.
	 * Disabling this will prevent any material additions from being applied
	 * and will prevent any recipes that use material additions from working.
	 */
	@Configurable(onSet = "com.bawnorton.trimica.Trimica#refreshEverything")
	public static boolean enableMaterialAdditions = true;

	public static DataComponentType<MaterialAdditions> TYPE;

	public static final Codec<MaterialAdditions> CODEC = ResourceLocation.CODEC.listOf().xmap(
			list -> new MaterialAdditions(List.copyOf(list)),
			materialAddition -> materialAddition == null ? List.of() : List.copyOf(materialAddition.additionKeys)
	);

	public static final StreamCodec<ByteBuf, MaterialAdditions> STREAM_CODEC = ByteBufCodecs.<ByteBuf, ResourceLocation>list().apply(ResourceLocation.STREAM_CODEC).map(
			list -> new MaterialAdditions(List.copyOf(list)),
			materialAddition -> materialAddition == null ? List.of() : List.copyOf(materialAddition.additionKeys)
	);

	public MaterialAdditions(ResourceLocation addition) {
		this(List.of(addition));
	}

	public static MaterialAdditions empty() {
		return new MaterialAdditions(new ArrayList<>());
	}

	public MaterialAdditions and(ResourceLocation addition) {
		List<ResourceLocation> additions = new ArrayList<>(this.additionKeys);
		if (additions.contains(addition)) return this;

		additions.add(addition);
		return new MaterialAdditions(additions);
	}

	public MaterialAdditions and(MaterialAdditions additions) {
		if (additions == null || additions.isEmpty()) return this;

		List<ResourceLocation> newAdditions = new ArrayList<>(this.additionKeys);
		for (ResourceLocation addition : additions.additionKeys) {
			if (!newAdditions.contains(addition)) {
				newAdditions.add(addition);
			}
		}
		return new MaterialAdditions(newAdditions);
	}

	public boolean matches(ResourceLocation key) {
		for (ResourceLocation additionKey : additionKeys) {
			if (key.equals(additionKey)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return additionKeys.isEmpty();
	}

	public ResourceLocation apply(ResourceLocation id) {
		if (additionKeys.isEmpty()) {
			return id;
		}
		String additionPrefix = "/additions/";
		String path = id.getPath();
		int index = path.indexOf(additionPrefix);
		StringBuilder newPath;
		if (index != -1) {
			newPath = new StringBuilder(path.substring(0, index + additionPrefix.length()));
		} else {
			newPath = new StringBuilder(path + additionPrefix);
		}
		for (ResourceLocation additionKey : additionKeys) {
			newPath.append(additionKey.getNamespace()).append("/").append(additionKey.getPath()).append("/");
		}
		return id.withPath(newPath.toString());
	}

	public static int removeMaterials(ItemStack stack, int count) {
		if (!enableMaterialAdditions) return count;

		MaterialAdditions additions = stack.get(MaterialAdditions.TYPE);
		if (additions == null || additions.additionKeys.isEmpty()) return count;

		List<ResourceLocation> newAdditions = new ArrayList<>(additions.additionKeys);
		for (; count > 0 && !newAdditions.isEmpty(); count--) {
			newAdditions.removeLast();
		}
		if (newAdditions.isEmpty()) {
			stack.remove(MaterialAdditions.TYPE);
		} else {
			stack.set(MaterialAdditions.TYPE, new MaterialAdditions(newAdditions));
		}
		return count;
	}

}
