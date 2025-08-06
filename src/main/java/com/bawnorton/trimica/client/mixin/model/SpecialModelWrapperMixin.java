package com.bawnorton.trimica.client.mixin.model;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.palette.TrimPalette;
import com.bawnorton.trimica.client.texture.DynamicTrimTextureAtlasSprite;
import com.bawnorton.trimica.compat.Compat;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.SpecialModelWrapper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@MixinEnvironment(value = "client")
@Mixin(SpecialModelWrapper.class)
public abstract class SpecialModelWrapperMixin {
    @Inject(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/special/SpecialModelRenderer;extractArgument(Lnet/minecraft/world/item/ItemStack;)Ljava/lang/Object;"
            )
    )
    private void applyPaletteToSpecialModelRenderState(ItemStackRenderState itemStackRenderState, ItemStack itemStack, ItemModelResolver itemModelResolver, ItemDisplayContext itemDisplayContext, ClientLevel clientLevel, LivingEntity livingEntity, int i, CallbackInfo ci) {
        List<DynamicTrimTextureAtlasSprite> dynamicSprites = TrimicaClient.getRuntimeAtlases().getShieldSprites(itemStack);
        if (dynamicSprites.isEmpty()) return;

        boolean anyAnimated = false;
        for (DynamicTrimTextureAtlasSprite sprite : dynamicSprites) {
            TrimPalette palette = sprite.getPalette();
            if(palette != null && palette.isAnimated()) {
                anyAnimated = true;
            }
        }
        if (!anyAnimated) return;

        //? if >1.21.5 {
        itemStackRenderState.setAnimated();
        //?}
        for (DynamicTrimTextureAtlasSprite sprite : dynamicSprites) {
            Compat.ifSodiumPresent(compat -> compat.markSpriteAsActive(sprite));
        }
    }
}
