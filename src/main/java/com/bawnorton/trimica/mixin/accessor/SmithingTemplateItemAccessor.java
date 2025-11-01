package com.bawnorton.trimica.mixin.accessor;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(SmithingTemplateItem.class)
public interface SmithingTemplateItemAccessor {
	@Invoker("createTrimmableMaterialIconList")
	static List<ResourceLocation> trimica$createTrimmableMaterialIconList() {
		throw new AssertionError();
	}

	@Accessor("DESCRIPTION_FORMAT")
	static ChatFormatting trimica$getDescriptionFormat() {
		throw new AssertionError();
	}

	@Accessor("EMPTY_SLOT_HOE")
	static ResourceLocation trimica$getEmptySlotHoe() {
		throw new AssertionError();
	}

	@Accessor("EMPTY_SLOT_AXE")
	static ResourceLocation trimica$getEmptySlotAxe() {
		throw new AssertionError();
	}

	@Accessor("EMPTY_SLOT_SWORD")
	static ResourceLocation trimica$getEmptySlotSword() {
		throw new AssertionError();
	}

	@Accessor("EMPTY_SLOT_SHOVEL")
	static ResourceLocation trimica$getEmptySlotShovel() {
		throw new AssertionError();
	}

	@Accessor("EMPTY_SLOT_PICKAXE")
	static ResourceLocation trimica$getEmptySlotPickaxe() {
		throw new AssertionError();
	}
}
