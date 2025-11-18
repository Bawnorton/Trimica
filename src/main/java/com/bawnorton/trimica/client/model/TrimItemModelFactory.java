package com.bawnorton.trimica.client.model;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.*;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.bawnorton.trimica.trim.TrimmedType;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TrimItemModelFactory {
	private final Map<ResourceLocation, ItemModel> modelCache = new HashMap<>();
	private final Map<ResourceLocation, TrimPalette> paletteCache = new HashMap<>();
	private final Map<ItemModel, ResourceLocation> baseModelLocations = new HashMap<>();
	private ModelManager.ResolvedModels resolvedModels;

	public TrimmedItemModelWrapper getOrCreateModel(ItemModel base, ClientLevel level, ItemStack stack, ArmorTrim trim) {
		TrimModelId trimModelId = getModelId(stack, trim);
		if (trimModelId == null) {
			return TrimmedItemModelWrapper.noTrim(base);
		}
		ResourceLocation overlayLocation = trimModelId.asSingle();
		if (MaterialAdditions.enableMaterialAdditions) {
			MaterialAdditions addition = stack.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
			overlayLocation = addition.apply(overlayLocation);
		}
		ResourceLocation baseModelLocation = baseModelLocations.computeIfAbsent(base, k ->
				stack.getOrDefault(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(stack.getItem()))
						.withPrefix("item/")
		);
		ResourceLocation newModelLocation = overlayLocation.withPrefix(
				baseModelLocation.toString().replace(":", "_") + "/"
		);
		if (modelCache.containsKey(newModelLocation)) {
			return new TrimmedItemModelWrapper(modelCache.get(newModelLocation), paletteCache.get(newModelLocation), newModelLocation);
		}
		ItemModel model = createModel(baseModelLocation, newModelLocation, overlayLocation, base, level, stack, trim);
		modelCache.put(newModelLocation, model);
		return new TrimmedItemModelWrapper(model, paletteCache.get(newModelLocation), newModelLocation);
	}

	public static TrimModelId getModelId(ItemStack stack, ArmorTrim trim) {
		Optional<ResourceKey<EquipmentAsset>> assetId = Optional.ofNullable(stack.get(DataComponents.EQUIPPABLE)).flatMap(Equippable::assetId);
		TrimmedType trimmedType = TrimmedType.of(stack);
		if (trimmedType == TrimmedType.UNKNOWN) return null;

		return TrimModelId.fromTrim(trimmedType, trim, assetId.orElse(null));
	}

	private ItemModel createModel(ResourceLocation baseModelLocation, ResourceLocation newModelLocation, ResourceLocation overlayLocation, ItemModel base, ClientLevel level, ItemStack stack, ArmorTrim trim) {
		ResolvedModel baseResolved = resolvedModels.models().get(baseModelLocation);
		if (baseResolved == null) {
			Trimica.LOGGER.error("Failed to find base resolved model: {}", baseModelLocation);
			return base;
		}
		TextureSlots.Data slots = baseResolved.wrapped().textureSlots();
		Map<String, TextureSlots.SlotContents> baseContents = slots.values();
		String targetLayer = findTargetLayer(baseContents);
		Map<String, TextureSlots.SlotContents> contents = ImmutableMap.<String, TextureSlots.SlotContents>builder()
				.putAll(slots.values())
				.put(targetLayer, TextureSlots$ValueAccessor.trimica$init(new Material(overlayLocation, overlayLocation)))
				.buildKeepingLast();
		UnbakedModel generatedModel = new BlockModel(
				null,
				UnbakedModel.GuiLight.FRONT,
				false,
				null,
				new TextureSlots.Data(contents),
				ResourceLocation.withDefaultNamespace("item/generated")
		);
		ResolvedModel resolvedModel = ModelDiscover$ModelWrapperAccessor.trimica$init(newModelLocation, generatedModel, true);
		((ModelDiscover$ModelWrapperAccessor) resolvedModel).trimica$parent((ModelDiscovery.ModelWrapper) baseResolved.parent());
		Minecraft minecraft = Minecraft.getInstance();
		ModelManager modelManager = minecraft.getModelManager();
		//? if >=1.21.10 {
		ModelBakery modelBakery = new ModelBakery(
				EntityModelSet.EMPTY,
				minecraft.getAtlasManager(),
				null,
				Map.of(),
				Map.of(),
				Map.of(newModelLocation, resolvedModel),
				ModelDiscover$ModelWrapperAccessor.trimica$init(MissingBlockModel.LOCATION, MissingBlockModel.missingModel(), true)
		);
		AtlasManager atlasManager = minecraft.getAtlasManager();
		//?} else {
		/*ModelBakery modelBakery = new ModelBakery(
				EntityModelSet.EMPTY,
				Map.of(),
				Map.of(),
				Map.of(newModelLocation, resolvedModel),
				ModelDiscover$ModelWrapperAccessor.trimica$init(MissingBlockModel.LOCATION, MissingBlockModel.missingModel(), true)
		);
		*///?}
		DynamicTrimTextureAtlasSprite sprite = TrimicaClient.getRuntimeAtlases()
				.getItemAtlas(level, trim.material().value())
				.getSprite(stack, trim.pattern().value(), overlayLocation);
		paletteCache.put(newModelLocation, sprite.getPalette());
		SpriteGetter spriteGetter = new SpriteGetter() {
			@Override
			public @NotNull TextureAtlasSprite get(Material material, @NotNull ModelDebugName modelDebugName) {
				if (material.texture().equals(overlayLocation)) {
					return sprite;
				}
				//? if >=1.21.10 {
				return atlasManager.get(material);
				//?} else {
				/*TextureAtlas atlas = modelManager.getAtlas(material.atlasLocation());
				return atlas.getSprite(material.texture());
				*///?}
			}

			@Override
			public @NotNull TextureAtlasSprite reportMissingReference(@NotNull String string, @NotNull ModelDebugName modelDebugName) {
				throw new IllegalStateException("Dynamic sprite missing: \"%s\" in model: \"%s\"".formatted(string, modelDebugName.debugName()));
			}
		};
		ModelBakery.MissingModels missingModels = ((ModelManagerAccessor) modelManager).trimica$missingModels();
		ItemModel.BakingContext bakingContext = new ItemModel.BakingContext(
				ModelBakery$ModelBakerImplAccessor.trimic$init(modelBakery, spriteGetter),
				EntityModelSet.EMPTY,
				//? if >=1.21.10 {
				atlasManager,
				null,
				//?}
				missingModels.item(),
				null
		);
		BlockModelWrapper.Unbaked unbaked = new BlockModelWrapper.Unbaked(newModelLocation, ((BlockModelWrapperAccessor) base).trimica$tints());
		return unbaked.bake(bakingContext);
	}

	private String findTargetLayer(Map<String, TextureSlots.SlotContents> baseContents) {
		int trimLayerIndex = -1;
		for (Map.Entry<String, TextureSlots.SlotContents> entry : baseContents.entrySet()) {
			String key = entry.getKey();
			TextureSlots.SlotContents content = entry.getValue();
			if (key.startsWith("layer")) {
				String texture = switch(content) {
					case TextureSlots.Value value -> value.material().texture().getPath();
					case TextureSlots.Reference reference -> reference.target();
				};
				if (texture.startsWith("trims/")) {
					trimLayerIndex = Integer.parseInt(key.substring("layer".length()));
					break;
				}
			}
		}
		String targetLayer;
		if (trimLayerIndex != -1) {
			targetLayer = "layer" + trimLayerIndex;
		} else {
			String largestLayer = baseContents.keySet().stream()
					.filter(key -> key.startsWith("layer"))
					.max(Comparator.comparingInt(a -> Integer.parseInt(a.substring("layer".length()))))
					.orElse("layer0");
			int largestIndex = Integer.parseInt(largestLayer.substring("layer".length()));
			targetLayer = "layer" + (largestIndex + 1);
		}
		return targetLayer;
	}

	public void setResolvedModels(ModelManager.ResolvedModels resolvedModels) {
		this.resolvedModels = resolvedModels;
	}

	public void registerBakedModel(ItemModel original, ResourceLocation model) {
		baseModelLocations.put(original, model);
	}

	public void clearModels() {
		modelCache.clear();
	}

	public void clear() {
		clearModels();
		((GuiRendererAccessor) ((GameRendererAccessor) Minecraft.getInstance().gameRenderer).trimica$guiRenderer()).trimica$invalidateItemAtlas();
		paletteCache.clear();
	}
}