package com.bawnorton.trimica.client.mixin.render;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.model.TrimModelId;
import com.bawnorton.trimica.client.texture.DynamicTextureAtlasSprite;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.special.ShieldSpecialRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShieldSpecialRenderer.class)
public abstract class ShieldSpecialRendererMixin {
    @Shadow @Final private ShieldModel model;

    @Inject(
            method = "render(Lnet/minecraft/core/component/DataComponentMap;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IIZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
    )
    private void renderTrim(DataComponentMap dataComponentMap, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, boolean bl, CallbackInfo ci) {
        ArmorTrim trim = dataComponentMap != null ? dataComponentMap.get(DataComponents.TRIM) : null;
        if (trim == null) return;

        TrimModelId trimModelId = TrimModelId.fromTrim(dataComponentMap, trim, Items.SHIELD, null);
        DynamicTextureAtlasSprite sprite = TrimicaClient.getRuntimeAtlases().getShieldAtlas(trim).getSprite(null, trimModelId.asSingle());
        VertexConsumer vertexConsumer = sprite.wrap(ItemRenderer.getFoilBuffer(multiBufferSource, sprite.getRenderType(), itemDisplayContext == ItemDisplayContext.GUI, bl));
        this.model.plate().render(poseStack, vertexConsumer, i, j);
    }
}
