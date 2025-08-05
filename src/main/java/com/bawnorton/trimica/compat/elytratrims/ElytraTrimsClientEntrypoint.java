package com.bawnorton.trimica.compat.elytratrims;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlas;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.compat.Compat;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kikugie.elytratrims.api.ETClientInitializer;
import dev.kikugie.elytratrims.api.render.ETRenderParameters;
import dev.kikugie.elytratrims.api.render.ETRenderingAPI;
import dev.kikugie.elytratrims.api.render.ETRenderingAPIUtils;
import dev.kikugie.elytratrims.render.impl.ETTrimsRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import java.util.function.Function;

//? if fabric {
@dev.kikugie.fletching_table.annotation.fabric.Entrypoint("elytratrims-client")
//?}
public class ElytraTrimsClientEntrypoint implements ETClientInitializer {
    @Override
    public void onInitializeClientET() {
        ETRenderingAPI.wrapRenderCall(ETTrimsRenderer.type, this::renderWithTrimica);
    }

    private boolean renderWithTrimica(ETRenderParameters parameters, Function<ETRenderParameters, Boolean> original) {
        ItemStack stack = parameters.stack();
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) {
            return original.apply(parameters);
        }

        RuntimeTrimAtlases atlases = TrimicaClient.getRuntimeAtlases();
        TrimPattern pattern = trim.pattern().value();
        RuntimeTrimAtlas atlas = atlases.getEquipmentAtlas(pattern, EquipmentClientInfo.LayerType.WINGS);
        if (atlas == null) {
            return original.apply(parameters);
        }

        ResourceLocation overlayLocation = trim.layerAssetId(EquipmentClientInfo.LayerType.WINGS.trimAssetPrefix(), EquipmentAssets.ELYTRA);
        MaterialAdditions additions = null;
        if (MaterialAdditions.enableMaterialAdditions) {
            additions = stack.get(MaterialAdditions.TYPE);
            if (additions != null) {
                overlayLocation = additions.apply(overlayLocation);
            }
        }

        TrimMaterial material = trim.material().value();
        DynamicTrimTextureAtlasSprite newSprite = atlas.getSprite(stack, material, overlayLocation);
        TrimPalette palette = newSprite.getPalette();
        if (palette == null || palette.isBuiltin() && additions == null) {
            return original.apply(parameters);
        }

        if (palette.isAnimated()) {
            Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(newSprite));
        }
        VertexConsumer vertexConsumer = newSprite.wrap(ItemRenderer.getArmorFoilBuffer(parameters.source(), newSprite.getRenderType(), stack.hasFoil()));
        int light = palette.isEmissive() ? LightTexture.FULL_BRIGHT : ETRenderingAPIUtils.getEffectiveLight(parameters);
        Model elytra = parameters.elytra();
        elytra.renderToBuffer(parameters.matrices(), vertexConsumer, light, OverlayTexture.NO_OVERLAY, parameters.color());
        return true;
    }
}
