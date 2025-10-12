package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@MixinEnvironment(value = "client")
@Mixin(value = GuiRenderer.class)
public interface GuiRendererAccessor {
	@Invoker("invalidateItemAtlas")
	void trimica$invalidateItemAtlas();
}