package com.bawnorton.trimica.client.palette;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.mixin.accessor.*;
import com.bawnorton.trimica.client.colour.ColourGroup;
import com.bawnorton.trimica.client.colour.ColourHSB;
import com.bawnorton.trimica.client.colour.OkLabHelper;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if fabric {
import com.bawnorton.trimica.platform.fabric.mixin.accessor.WrapperBakedItemModelAccessor;
//?}

public final class TrimPaletteGenerator {
    private static final Map<ResourceLocation, TrimPalette> TRIM_PALETTES = new HashMap<>();
    private static final Map<String, TrimPalette> BUILT_IN_PALETTES = new HashMap<>();

    public TrimPalette generatePalette(TrimMaterial material, String suffix, ResourceLocation location) {
        Item materialProvider = Trimica.getMaterialRegistry().getMaterialProvider(suffix);
        if (materialProvider == null) {
            return generatePaletteFromBuiltIn(material, suffix);
        }
        ItemModelResolver modelResolver = Minecraft.getInstance().getItemModelResolver();
        ItemModel model = ((ItemModelResolverAccessor) modelResolver).trimica$modelGetter().apply(BuiltInRegistries.ITEM.getKey(materialProvider));
        return generatePaletteFromModel(location, model);
    }

    private TrimPalette generatePaletteFromBuiltIn(TrimMaterial material, String suffix) {
        return BUILT_IN_PALETTES.computeIfAbsent(suffix, k -> {
            List<Integer> colours = getColoursFromBuiltIn(material, suffix);
            if(colours.isEmpty()) {
                Trimica.LOGGER.warn("Trim palette colour could not determined for builtin material {}", suffix);
                return TrimPalette.DEFAULT;
            }
            return new TrimPalette(colours, true);
        });
    }

    private List<Integer> getColoursFromBuiltIn(TrimMaterial material, String suffix) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager resourceManager = minecraft.getResourceManager();
        ClientLevel level = minecraft.level;
        if (level == null) return List.of();

        Registry<TrimMaterial> lookup = level.registryAccess().lookup(Registries.TRIM_MATERIAL).orElse(null);
        if (lookup == null) return List.of();

        ResourceLocation materialId = lookup.getKey(material);
        if (materialId == null) return List.of();

        try (TextureContents contents = TextureContents.load(resourceManager, materialId.withPath("textures/trims/color_palettes/%s.png".formatted(suffix)))) {
            NativeImage image = contents.image();
            return extractColoursFromBuiltIn(image);
        } catch (IOException e) {
            Trimica.LOGGER.error("Failed to load trim palette texture", e);
            return List.of();
        }
    }

    private List<Integer> extractColoursFromBuiltIn(NativeImage builtInImage) {
        int width = builtInImage.getWidth();
        int height = builtInImage.getHeight();
        if (width != 8 || height != 1) {
            return List.of();
        }
        List<Integer> colours = new ArrayList<>(8);
        for (int x = 0; x < width; x++) {
            int colour = builtInImage.getPixel(x, 0);
            colours.add(ARGB.toABGR(colour));
        }
        return colours;
    }

    private TrimPalette generatePaletteFromModel(ResourceLocation location, ItemModel model) {
        return TRIM_PALETTES.computeIfAbsent(location, key -> {
            List<Integer> colours = getColoursFromModel(model);
            if(colours.isEmpty()) {
                Trimica.LOGGER.warn("Trim palette colour could not determined for {}", location);
                return TrimPalette.DEFAULT;
            }
            colours = getDominantColours(colours);
            colours = sortPalette(colours);
            colours = stretchPalette(colours);
            return new TrimPalette(colours);
        });
    }

    private List<Integer> getColoursFromModel(ItemModel model) {
        return switch (model) {
            case BlockModelWrapperAccessor blockModelWrapperAccessor -> getColoursFromQuads(blockModelWrapperAccessor.trimica$quads());
            case SelectItemModelAccessor selectItemModelAccessor -> getColoursFromModel(selectItemModelAccessor.trimica$models().get(null, null));
            case SpecialModelWrapperAccessor specialModelWrapperAccessor -> {
                ModelRenderProperties properties = specialModelWrapperAccessor.trimica$properties();
                int[] colours = extractColours(properties.particleIcon());
                yield Arrays.stream(colours).boxed().toList();
            }
            case CompositeModelAccessor compositeModelAccessor -> getColoursFromModel(compositeModelAccessor.trimica$models().getFirst());
            case ConditionalItemModelAccessor conditionalItemModelAccessor -> getColoursFromModel(conditionalItemModelAccessor.trimica$onFalse());
            case RangeSelectItemModelAccessor rangeSelectItemModelAccessor -> getColoursFromModel(rangeSelectItemModelAccessor.trimica$fallback());
            //? if fabric {
            case WrapperBakedItemModelAccessor wrapperBakedItemModelAccessor -> getColoursFromModel(wrapperBakedItemModelAccessor.trimica$wrapped());
            //?}
            case null, default -> Collections.emptyList();
        };
    }

    private List<Integer> getDominantColours(List<Integer> colours) {
        List<ColourHSB> hsbColours = ColourHSB.fromRGB(colours);

        List<ColourGroup> groups = new ArrayList<>();
        for (ColourHSB colour : hsbColours) {
            boolean foundGroup = false;
            for (ColourGroup group : groups) {
                if (group.isSimilar(colour)) {
                    group.addMember(colour);
                    foundGroup = true;
                    break;
                }
            }
            if (!foundGroup) {
                groups.add(new ColourGroup(colour));
            }
        }
        Collections.sort(groups);
        List<ColourHSB> dominantColours = new ArrayList<>();
        int count = 0;
        for (ColourGroup group : groups) {
            if(count < TrimPalette.PALETTE_SIZE) {
                dominantColours.add(group.getRepresentative());
                count++;
            } else {
                break;
            }
        }
        List<Integer> dominantRGB = new ArrayList<>();
        for (ColourHSB colour : dominantColours) {
            dominantRGB.add(colour.colour());
        }
        return dominantRGB;
    }

    private List<Integer> sortPalette(List<Integer> colours) {
        List<ColourHSB> hsbColours = ColourHSB.fromRGB(colours);

        return hsbColours.stream()
                         .sorted()
                         .map(ColourHSB::colour)
                         .toList();
    }

    private List<Integer> stretchPalette(List<Integer> palette) {
        int size = palette.size();
        int targetSize = TrimPalette.PALETTE_SIZE;
        if (size >= targetSize) {
            return palette;
        }

        List<double[]> oklabPalette = OkLabHelper.rgbToOklab(palette);
        List<double[]> stretchedOKLab = OkLabHelper.strechOkLab(targetSize, size, oklabPalette);
        return OkLabHelper.okLabToRgb(stretchedOKLab);
    }

    private @NotNull List<Integer> getColoursFromQuads(List<BakedQuad> quads) {
        List<Integer> colours = new ArrayList<>(quads.size() * 16 * 16);
        for (BakedQuad bakedQuad : quads) {
            int[] colourData = extractColours(bakedQuad.sprite());
            for (int colour : colourData) {
                colours.add(colour);
            }
        }
        return colours.stream().filter(i -> i != 0).toList();
    }

    @SuppressWarnings("resource")
    private int[] extractColours(TextureAtlasSprite sprite) {
        NativeImage spriteImage = ((SpriteContentsAccessor) sprite.contents()).trimica$originalImage();
        int width = spriteImage.getWidth();
        int height = spriteImage.getHeight();

        int[] colourData = new int[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int colour = ARGB.fromABGR(spriteImage.getPixel(x, y));
                int alpha = ARGB.alpha(colour);
                if (alpha == 0) {
                    continue;
                }

                int red = ARGB.red(colour);
                int green = ARGB.green(colour);
                int blue = ARGB.blue(colour);
                int packed = red << 16 | green << 8 | blue;
                colourData[x + y * width] = packed;
            }
        }

        return colourData;
    }
}
