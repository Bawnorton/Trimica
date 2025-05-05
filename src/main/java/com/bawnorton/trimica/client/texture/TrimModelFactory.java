package com.bawnorton.trimica.client.texture;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.mixin.accessor.BlockModelWrapperAccessor;
import com.bawnorton.trimica.client.mixin.accessor.ItemModelResolverAccessor;
import com.bawnorton.trimica.client.mixin.accessor.SelectItemModelAccessor;
import com.bawnorton.trimica.client.texture.palette.TrimPalette;
import com.bawnorton.trimica.trim.material.RuntimeTrimMaterials;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import java.util.List;

public class TrimModelFactory implements RuntimeTrimSpriteFactory {
    private static final int WIDTH = 64;
    private static final int HEIGHT = 32;

    @Override
    public DynamicTextureAtlasSprite apply(ArmorTrim trim, ResourceLocation location) {
        return DynamicTextureAtlasSprite.create(
                location,
                createImageForModel(trim, location),
                WIDTH,
                HEIGHT,
                trim.pattern().value().decal()
        );
    }

    private NativeImage createImageForModel(ArmorTrim trim, ResourceLocation location) {
        ItemModelResolver modelResolver = Minecraft.getInstance().getItemModelResolver();
        Item materialProvider = RuntimeTrimMaterials.getMaterialProvider(trim.material().value());
        if (materialProvider == null) {
            return empty();
        }
        ItemModel model = ((ItemModelResolverAccessor) modelResolver).trimica$modelGetter().apply(BuiltInRegistries.ITEM.getKey(materialProvider));
        List<BakedQuad> quads = getQuads(model);
        return createImageFromQuads(trim, location, quads);
    }

    private List<BakedQuad> getQuads(ItemModel model) {
        return switch (model) {
            case BlockModelWrapperAccessor blockModelWrapperAccessor -> blockModelWrapperAccessor.trimica$quads();
            case SelectItemModelAccessor selectItemModelAccessor -> getQuads(selectItemModelAccessor.trimica$models().get(null, null));
            // TODO: Handle other model types
            case null, default -> List.of();
        };
    }

    private NativeImage createImageFromQuads(ArmorTrim trim, ResourceLocation location, List<BakedQuad> quads) {
        TrimPalette palette = TrimicaClient.getPaletteGenerator().generatePalette(location, quads);
        NativeImage image = new NativeImage(WIDTH, HEIGHT, false);
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                image.setPixel(x, y, 0xFFFF0000);
            }
        }
        return image;
    }

    private NativeImage empty() {
        return new NativeImage(WIDTH, HEIGHT, false);
    }
}
