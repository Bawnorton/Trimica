package com.bawnorton.trimica.client.mixin.compat;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlas;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.client.texture.TrimArmourSpriteFactory;
import com.bawnorton.trimica.compat.Compat;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.elytratrims.render.ETRenderer;
import dev.kikugie.elytratrims.render.ETTrimsRenderer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@MixinEnvironment(value = "client")
@Mixin(ETTrimsRenderer.class)
public abstract class ETTrimsRendererMixin {
    @WrapOperation(
            method = "trimCache$lambda$0",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/kikugie/elytratrims/render/ETRenderer$Companion;getSpriteReporting(Ldev/kikugie/elytratrims/render/ETRenderer;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
            )
    )
    private static TextureAtlasSprite provideRuntimeTrimSprites(ETRenderer.Companion instance, ETRenderer renderer, ResourceLocation $i$a$_also_ETRenderer$Companion$getSpriteReporting$2, ResourceLocation texture, Operation<TextureAtlasSprite> original, ArmorTrim trim) {
        TextureAtlasSprite sprite = original.call(instance, renderer, $i$a$_also_ETRenderer$Companion$getSpriteReporting$2, texture);
        TrimMaterial material = trim.material().value();
        ItemStack stack = TrimArmourSpriteFactory.ITEM_WITH_TRIM_CAPTURE.get();
        if(stack == null) return sprite;

        MaterialAdditions addition = stack.get(MaterialAdditions.TYPE);
        if (sprite != null && addition == null) return sprite;

        RuntimeTrimAtlases atlases = TrimicaClient.getRuntimeAtlases();
        TrimPattern pattern = trim.pattern().value();
        RuntimeTrimAtlas atlas = atlases.getEquipmentAtlas(pattern, EquipmentClientInfo.LayerType.WINGS);
        if (atlas == null) return sprite;

        ResourceLocation overlayLocation = texture;
        if (addition != null) {
            overlayLocation = addition.apply(overlayLocation);
        }
        return atlas.getSprite(stack, material, overlayLocation);
    }
}
