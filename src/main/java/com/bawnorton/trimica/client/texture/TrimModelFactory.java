package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.BlockModelWrapperAccessor;
import com.bawnorton.trimica.client.mixin.accessor.ItemModelResolverAccessor;
import com.bawnorton.trimica.client.mixin.accessor.SelectItemModelAccessor;
import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import com.bawnorton.trimica.trim.material.RuntimeTrimMaterials;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.joml.Vector2i;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrimModelFactory implements RuntimeTrimSpriteFactory {
    private static final int WIDTH = 64;
    private static final int HEIGHT = 32;

    @Override
    public DynamicTextureAtlasSprite apply(ArmorTrim trim, ResourceLocation location) {
        return DynamicTextureAtlasSprite.create(
                location,
                createImageForModel(trim.material().value(), location),
                WIDTH,
                HEIGHT,
                trim.pattern().value().decal()
        );
    }

    private NativeImage createImageForModel(TrimMaterial material, ResourceLocation location) {
        ItemModelResolver modelResolver = Minecraft.getInstance().getItemModelResolver();
        Item materialProvider = RuntimeTrimMaterials.getMaterialProvider(material);
        if (materialProvider == null) {
            return empty();
        }
        ItemModel model = ((ItemModelResolverAccessor) modelResolver).trimica$modelGetter().apply(BuiltInRegistries.ITEM.getKey(materialProvider));
        List<BakedQuad> quads = getQuads(model);
        return createImageFromQuads(location, quads);
    }

    private List<BakedQuad> getQuads(ItemModel model) {
        return switch (model) {
            case BlockModelWrapperAccessor blockModelWrapperAccessor -> blockModelWrapperAccessor.trimica$quads();
            case SelectItemModelAccessor selectItemModelAccessor -> getQuads(selectItemModelAccessor.trimica$models().get(null, null));
            // TODO: Handle other model types
            case null, default -> List.of();
        };
    }

    private NativeImage createImageFromQuads(ResourceLocation location, List<BakedQuad> quads) {
        ResourceLocation basePatternTexture = extractBaseTexture(location);
        if (basePatternTexture == null) return empty();

        Minecraft minecraft = Minecraft.getInstance();
        try {
            TextureContents contents = TextureContents.load(minecraft.getResourceManager(), basePatternTexture);
            TrimPalette palette = TrimicaClient.getPaletteGenerator().generatePalette(location, quads);
            NativeImage coloured = createColouredPatternImage(contents.image(), palette);
            contents.close();
            return coloured;
        } catch (IOException e) {
            Trimica.LOGGER.error("Failed to load texture", e);
            return empty();
        }
    }

    private NativeImage createColouredPatternImage(NativeImage greyscalePatternImage, TrimPalette palette) {
        NativeImage coloured = new NativeImage(WIDTH, HEIGHT, false);
        Map<Integer, Set<Vector2i>> colourPositions = new Int2ObjectAVLTreeMap<>(Comparator.comparingInt(i -> i));
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int pixel = greyscalePatternImage.getPixel(x, y);
                colourPositions.computeIfAbsent(pixel, k -> new HashSet<>()).add(new Vector2i(x, y));
            }
        }
        int index = 0;
        for (Map.Entry<Integer, Set<Vector2i>> entry : colourPositions.entrySet()) {
            int colour = entry.getKey();
            int paletteColour;
            if (colour == 0 || index >= TrimPalette.PALETTE_SIZE) {
                paletteColour = 0;
            } else {
                paletteColour = ARGB.toABGR(0xFF000000 | palette.getColours().get(index));
            }
            Set<Vector2i> positions = entry.getValue();
            for (Vector2i position : positions) {
                int x = position.x();
                int y = position.y();
                coloured.setPixel(x, y, paletteColour);
            }
            index++;
        }
        return coloured;
    }

    private ResourceLocation extractBaseTexture(ResourceLocation location) {
        String path = location.getPath();
        int lastSlash = path.lastIndexOf("_/");
        if (lastSlash != -1) {
            return location.withPath("textures/%s.png".formatted(path.substring(0, lastSlash)));
        }
        return null;
    }

    private NativeImage empty() {
        NativeImage image = new NativeImage(WIDTH, HEIGHT, false);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                image.setPixel(x, y, 0);
            }
        }
        return image;
    }
}
