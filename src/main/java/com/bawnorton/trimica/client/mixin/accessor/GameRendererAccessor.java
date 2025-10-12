package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment(value = "client")
@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
	@Accessor("guiRenderer")
	GuiRenderer trimica$guiRenderer();
}