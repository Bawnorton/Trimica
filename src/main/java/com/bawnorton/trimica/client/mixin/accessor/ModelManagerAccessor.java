package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment(value = "client")
@Mixin(ModelManager.class)
public interface ModelManagerAccessor {
	@Accessor("missingModels")
	ModelBakery.MissingModels trimica$missingModels();
}
