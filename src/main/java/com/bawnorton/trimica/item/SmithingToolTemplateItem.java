package com.bawnorton.trimica.item;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.mixin.accessor.SmithingTemplateItemAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class SmithingToolTemplateItem extends SmithingTemplateItem {
	private static final ChatFormatting DESCRIPTION_FORMAT = SmithingTemplateItemAccessor.trimica$getDescriptionFormat();

	private static final Component TOOL_TRIM_APPLIES_TO = Component.translatable(
					Util.makeDescriptionId("item", Trimica.rl("smithing_template.tool_trim.applies_to"))
			)
			.withStyle(DESCRIPTION_FORMAT);
	private static final Component TOOL_TRIM_INGREDIENTS = Component.translatable(
					Util.makeDescriptionId("item", Trimica.rl("smithing_template.tool_trim.ingredients"))
			)
			.withStyle(DESCRIPTION_FORMAT);
	private static final Component TOOL_TRIM_BASE_SLOT_DESCRIPTION = Component.translatable(
			Util.makeDescriptionId("item", Trimica.rl("smithing_template.tool_trim.base_slot_description"))
	);
	private static final Component TOOL_TRIM_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(
			Util.makeDescriptionId("item", Trimica.rl("smithing_template.tool_trim.additions_slot_description"))
	);
	
	public SmithingToolTemplateItem(Properties properties) {
		super(
				TOOL_TRIM_APPLIES_TO,
				TOOL_TRIM_INGREDIENTS,
				TOOL_TRIM_BASE_SLOT_DESCRIPTION,
				TOOL_TRIM_ADDITIONS_SLOT_DESCRIPTION,
				createTrimmableToolIconList(),
				SmithingTemplateItemAccessor.trimica$createTrimmableMaterialIconList(),
				properties
		);
	}

	private static List<ResourceLocation> createTrimmableToolIconList() {
		return List.of(
				SmithingTemplateItemAccessor.trimica$getEmptySlotHoe(),
				SmithingTemplateItemAccessor.trimica$getEmptySlotAxe(),
				SmithingTemplateItemAccessor.trimica$getEmptySlotSword(),
				SmithingTemplateItemAccessor.trimica$getEmptySlotShovel(),
				SmithingTemplateItemAccessor.trimica$getEmptySlotPickaxe()
		);
	}
}
