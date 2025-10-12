//? if >=1.21.10 {
package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@MixinEnvironment(value = "client")
@Mixin(AtlasManager.PendingStitchResults.class)
public interface AtlasManager$PendingStitchResultsAccessor {
	@Invoker("<init>")
	static AtlasManager.PendingStitchResults trimica$init(List<AtlasManager.PendingStitch> pendingStitches, Map<ResourceLocation, CompletableFuture<SpriteLoader.Preparations>> stitchFutureById, CompletableFuture<?> allReadyToUpload) {
		throw new AssertionError();
	}

	@Accessor("pendingStitches")
	List<AtlasManager.PendingStitch> trimica$pendingStitches();

	@Accessor("allReadyToUpload")
	CompletableFuture<?> trimica$allReadyToUpload();
}
//?}