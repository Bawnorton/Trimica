package com.bawnorton.trimica.client.mixin.render;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.GameRendererAccessor;
import com.bawnorton.trimica.client.mixin.accessor.GuiRendererAccessor;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment("client")
@Mixin(GuiRenderer.class)
abstract class GuiRendererMixin {
	@Shadow
	protected abstract void invalidateItemAtlas();

	@Shadow
	protected abstract int getGuiScaleInvalidatingItemAtlasIfChanged();

	@Shadow
	protected abstract int calculateAtlasSizeInPixels(int itemWidth);

	@Shadow
	protected abstract void createAtlasTextures(int atlasSize);

	@Inject(
			method = "render",
			at = @At("TAIL")
	)
	private void resetUVs(GpuBufferSlice fogUniforms, CallbackInfo ci) {
		if (TrimicaClient.getRuntimeAtlases().shouldResetUVs()) {
			TrimicaClient.getRuntimeAtlases().setShouldResetUVs(false);
			invalidateItemAtlas();
			int guiScale = getGuiScaleInvalidatingItemAtlasIfChanged();
			int itemWidth = 16 * guiScale;
			int atlasSize = calculateAtlasSizeInPixels(itemWidth);
			createAtlasTextures(atlasSize);
		}
	}
}
