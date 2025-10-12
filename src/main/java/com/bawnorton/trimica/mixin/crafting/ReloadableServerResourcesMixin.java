package com.bawnorton.trimica.mixin.crafting;

import com.bawnorton.trimica.mixin.accessor.ReloadableServerResourcesAccessor;
import com.bawnorton.trimica.tags.PostponedTagHolder;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin {
	@ModifyExpressionValue(
			//? if fabric {
			method = "method_58296",
			//?} else {
			/*method = "lambda$loadResources$3",
			*///?}
			at = @At(
					value = "NEW",
					target = "(Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;Ljava/util/List;I)Lnet/minecraft/server/ReloadableServerResources;"
			)
	)
	private static ReloadableServerResources capturePostponedTags(ReloadableServerResources original) {
		PostponedTagHolder.setPostponedTags(((ReloadableServerResourcesAccessor) original).trimica$postponedTags());
		return original;
	}
}
