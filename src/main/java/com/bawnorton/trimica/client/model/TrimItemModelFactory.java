package com.bawnorton.trimica.client.model;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.BlockModelWrapperAccessor;
import com.bawnorton.trimica.client.mixin.accessor.ModelBakery$ModelBakerImplAccessor;
import com.bawnorton.trimica.client.mixin.accessor.ModelDiscover$ModelWrapperAccessor;
import com.bawnorton.trimica.client.mixin.accessor.ModelManagerAccessor;
import com.bawnorton.trimica.client.mixin.accessor.TextureSlots$ValueAccessor;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.item.component.ComponentUtil;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
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
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

//? if >1.21.5 {
import com.bawnorton.trimica.client.mixin.accessor.GameRendererAccessor;
import com.bawnorton.trimica.client.mixin.accessor.GuiRendererAccessor;
//?}

public final class TrimItemModelFactory {
    private final Map<ResourceLocation, ItemModel> models = new HashMap<>();
    private final Map<ResourceLocation, TrimPalette> palettes = new HashMap<>();
    private ModelManager.ResolvedModels resolvedModels;

    public TrimmedItemModelWrapper getOrCreateModel(ItemModel base, ItemStack stack, ArmorTrim trim) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return TrimmedItemModelWrapper.noTrim(base);
        }
        Optional<ResourceKey<EquipmentAsset>> assetId = equippable.assetId();
        ArmorType armourType = ComponentUtil.getArmourType(stack);
        if (armourType == null) {
            return TrimmedItemModelWrapper.noTrim(base);
        }
        TrimModelId trimModelId = TrimModelId.fromTrim(armourType.getName(), trim, assetId.orElse(null));
        ResourceLocation overlayLocation = trimModelId.asSingle();
        if (MaterialAdditions.enableMaterialAdditions) {
            MaterialAdditions addition = stack.get(MaterialAdditions.TYPE);
            if(addition != null) {
                overlayLocation = addition.apply(overlayLocation);
            }
        }
        ResourceLocation baseModelLocation = stack.getOrDefault(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(stack.getItem()));
        ResourceLocation newModelLocation = overlayLocation.withPrefix(baseModelLocation.toString().replace(":", "_") + "/");
        if (models.containsKey(newModelLocation)) {
            return new TrimmedItemModelWrapper(models.get(newModelLocation), palettes.get(newModelLocation), newModelLocation);
        }
        ItemModel model = createModel(baseModelLocation, newModelLocation, overlayLocation, base, stack, trim);
        models.put(newModelLocation, model);
        return new TrimmedItemModelWrapper(model, palettes.get(newModelLocation), newModelLocation);
    }

    private ItemModel createModel(ResourceLocation baseModelLocation, ResourceLocation newModelLocation, ResourceLocation overlayLocation, ItemModel base, ItemStack stack, ArmorTrim trim) {
        ResolvedModel baseResolved = resolvedModels.models().get(baseModelLocation.withPrefix("item/"));
        if (baseResolved == null) {
            Trimica.LOGGER.error("Failed to find base resolved model for trimmed item: {}", baseModelLocation);
            return base;
        }
        TextureSlots.Data slots = baseResolved.wrapped().textureSlots();
        Map<String, TextureSlots.SlotContents> baseContents = slots.values();
        String largestLayer = baseContents.keySet().stream()
                                          .filter(key -> key.startsWith("layer"))
                                          .max(Comparator.comparingInt(a -> Integer.parseInt(a.substring("layer".length()))))
                                          .orElse("layer0");
        String nextLayer = "layer" + (Integer.parseInt(largestLayer.substring("layer".length())) + 1);
        Map<String, TextureSlots.SlotContents> contents = ImmutableMap.<String, TextureSlots.SlotContents>builder()
                                                                      .putAll(slots.values())
                                                                      .put(nextLayer, TextureSlots$ValueAccessor.trimica$init(new Material(overlayLocation, overlayLocation)))
                                                                      .build();
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
        ModelBakery modelBakery = new ModelBakery(
                EntityModelSet.EMPTY,
                Map.of(),
                Map.of(),
                Map.of(newModelLocation, resolvedModel),
                ModelDiscover$ModelWrapperAccessor.trimica$init(MissingBlockModel.LOCATION, MissingBlockModel.missingModel(), true)
        );
        DynamicTrimTextureAtlasSprite sprite = TrimicaClient.getRuntimeAtlases()
                                                            .getItemAtlas(trim.pattern().value())
                                                            .getSprite(stack, trim.material().value(), overlayLocation);
        palettes.put(newModelLocation, sprite.getPalette());
        Minecraft minecraft = Minecraft.getInstance();
        ModelManager modelManager = minecraft.getModelManager();
        SpriteGetter spriteGetter = new SpriteGetter() {
            @Override
            public @NotNull TextureAtlasSprite get(Material material, @NotNull ModelDebugName modelDebugName) {
                if (material.texture().equals(overlayLocation)) {
                    return sprite;
                }
                TextureAtlas atlas = modelManager.getAtlas(material.atlasLocation());
                return atlas.getSprite(material.texture());
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
                missingModels.item(),
                null
        );
        BlockModelWrapper.Unbaked unbaked = new BlockModelWrapper.Unbaked(newModelLocation, ((BlockModelWrapperAccessor) base).trimica$tints());
        return unbaked.bake(bakingContext);
    }

    public void setResolvedModels(ModelManager.ResolvedModels resolvedModels) {
        this.resolvedModels = resolvedModels;
    }

    public void clearModels() {
        models.clear();
    }

    public void clear() {
        clearModels();
        //? if >1.21.5 {
        ((GuiRendererAccessor) ((GameRendererAccessor) Minecraft.getInstance().gameRenderer).trimica$guiRenderer()).trimica$invalidateItemAtlas();
        //?}
        palettes.clear();
    }
}
