package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.mixin.accessor.*;
import com.bawnorton.trimica.client.palette.TrimPalette;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

//? if >=1.21.10 {
import net.minecraft.client.resources.model.AtlasManager;
//?} else {
/*import net.minecraft.client.resources.model.AtlasSet;
*///?}

public final class RuntimeTrimAtlas extends TextureAtlas {
	private final AbstractTrimSpriteFactory spriteFactory;
	private final RuntimeTrimAtlases.TrimFactory trimFactory;
	private final RenderType renderType;
	private final List<SpriteContents> dynamicSprites = new ArrayList<>();
	private final Map<ResourceLocation, TrimPalette> palettes = new HashMap<>();
	private final Consumer<RuntimeTrimAtlas> onModified;
	//? if >=1.21.10
	private final AtlasManager.AtlasEntry atlasEntry;

	public RuntimeTrimAtlas(ResourceLocation atlasLocation, AbstractTrimSpriteFactory spriteFactory, RuntimeTrimAtlases.TrimFactory trimFactory, Consumer<RuntimeTrimAtlas> onModified) {
		super(atlasLocation);
		this.spriteFactory = spriteFactory;
		this.trimFactory = trimFactory;
		this.renderType = RenderType.armorCutoutNoCull(atlasLocation);
		dynamicSprites.add(createMissing());
		this.onModified = onModified;
		//? if >=1.21.10 {
		AtlasManager.AtlasConfig atlasConfig = new AtlasManager.AtlasConfig(atlasLocation, Trimica.rl("generated"), false);
		this.atlasEntry = new AtlasManager.AtlasEntry(this, atlasConfig);
		//?}
	}

	private SpriteContents createMissing() {
		ResourceLocation missingLocation = MissingTextureAtlasSprite.getLocation();
		return spriteFactory.create(missingLocation, null, null).spriteContents();
	}

	@Override
	public @NotNull TextureAtlasSprite getSprite(@NotNull ResourceLocation texture) {
		throw new UnsupportedOperationException("Use getSprite(DataComponentGetter, TrimMaterial, ResourceLocation) instead");
	}

	public @NotNull DynamicTrimTextureAtlasSprite getSprite(DataComponentGetter componentGetter, TrimPattern pattern, ResourceLocation texture) {
		Map<ResourceLocation, TextureAtlasSprite> texturesByName = asAccessor().trimica$texturesByName();
		TextureAtlasSprite sprite = texturesByName.get(texture);
		if (sprite == null) sprite = createSprite(componentGetter, pattern, texture);

		return new DynamicTrimTextureAtlasSprite(sprite, renderType, palettes.get(texture));
	}

	private DynamicTrimTextureAtlasSprite createSprite(DataComponentGetter componentGetter, TrimPattern pattern, ResourceLocation texture) {
		TrimSpriteContents sprite = spriteFactory.create(texture, trimFactory.create(pattern), componentGetter);
		dynamicSprites.add(sprite.spriteContents());
		stitchAndUpload();
		onModified.accept(this);
		Minecraft client = Minecraft.getInstance();
		client.getTextureManager().register(location(), this);
		palettes.put(texture, sprite.palette());
		return new DynamicTrimTextureAtlasSprite(asAccessor().trimica$texturesByName().get(texture), renderType, sprite.palette());
	}

	//? if >=1.21.10 {
	private void stitchAndUpload() {
		List<AtlasManager.PendingStitch> pendingStitches = new ArrayList<>();
		Map<ResourceLocation, CompletableFuture<SpriteLoader.Preparations>> stitchFutureById = new HashMap<>();
		List<CompletableFuture<?>> allReadyToUpload = new ArrayList<>();
		CompletableFuture<SpriteLoader.Preparations> preparations = new CompletableFuture<>();
		stitchFutureById.put(location(), preparations);
		pendingStitches.add(new AtlasManager.PendingStitch(atlasEntry, preparations));
		allReadyToUpload.add(preparations.thenCompose(SpriteLoader.Preparations::readyForUpload));
		AtlasManager$PendingStitchResultsAccessor pendingStitchResults = (AtlasManager$PendingStitchResultsAccessor) AtlasManager$PendingStitchResultsAccessor.trimica$init(
				pendingStitches,
				stitchFutureById,
				CompletableFuture.allOf(allReadyToUpload.toArray(new CompletableFuture[0]))
		);
		SpriteLoaderAccessor loader = (SpriteLoaderAccessor) SpriteLoader.create(this);
		pendingStitchResults.trimica$pendingStitches().forEach(stitch -> {
			SpriteLoader.Preparations stitched = loader.trimica$stitch(dynamicSprites, 0, Util.backgroundExecutor());
			if(stitched != null) {
				stitch.preparations().complete(stitched);
			} else {
				stitch.preparations().completeExceptionally(new RuntimeException("Failed to stitch dynamic trim atlas"));
			}
		});
		pendingStitchResults.trimica$allReadyToUpload()
				.thenApply(o -> ((AtlasManager.PendingStitchResults) pendingStitchResults).joinAndUpload())
				.join();
	}
	//?} else {
	/*private void stitchAndUpload() {
		SpriteLoader loader = SpriteLoader.create(this);
		SpriteLoader.Preparations preparations = loader.stitch(dynamicSprites, 0, Util.backgroundExecutor());
		AtlasSet.StitchResult result = new AtlasSet.StitchResult(this, preparations);
		result.upload();
	}
	*///?}

	/**
	 * @implNote Doesn't close or clear dynamic sprites, as {@link #stitchAndUpload} calls {@code clearTextureData} - we
	 * don't want to lose our previously computed dynamic sprites every time a new one is added
	 */
	public void clearTextureData() {
		TextureAtlasAccessor accessor = asAccessor();
		accessor.trimica$sprites(List.of());
		accessor.trimica$animatedTextures(List.of());
		accessor.trimica$texturesByName(Map.of());
		accessor.trimica$missingSprite(null);
	}

	/**
	 * Ensure's all animated textures are in-sync
	 */
	@SuppressWarnings("resource")
	public void resetFrames() {
		List<TextureAtlasSprite.Ticker> tickers = asAccessor().trimica$animatedTextures();
		for (TextureAtlasSprite.Ticker ticker : tickers) {
			if (ticker instanceof TextureAtlasSprite$TickerAccessor accessor && accessor.trimica$ticker() instanceof SpriteContents$TickerAccessor spriteTicker) {
				spriteTicker.trimica$frame(0);
			}
		}
	}

	private TextureAtlasAccessor asAccessor() {
		return (TextureAtlasAccessor) (Object) this;
	}

	public void clear() {
		spriteFactory.clear();
		dynamicSprites.clear();
		dynamicSprites.add(createMissing());
		clearTextureData();
	}
}