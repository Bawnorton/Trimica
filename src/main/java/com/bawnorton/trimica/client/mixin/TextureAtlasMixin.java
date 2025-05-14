package com.bawnorton.trimica.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    @Shadow @Final private ResourceLocation location;
    @Shadow private int mipLevel;

    @Unique
    private int trimica$lastWidth;

    @Unique
    private int trimica$lastHeight;

    @WrapOperation(
            method = "upload",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V",
                    remap = false
            )
    )
    private void betterLoggingInfoOnRepeatedUploads(Logger instance, String s, Object[] objects, Operation<Void> original, SpriteLoader.Preparations preparations) {
        int width = preparations.width();
        int height = preparations.height();

        if (trimica$lastHeight == 0 && trimica$lastWidth == 0) {
            trimica$lastHeight = height;
            trimica$lastWidth = width;
            original.call(instance, s, objects);
        } else if (width != trimica$lastWidth || height != trimica$lastHeight) {
            trimica$lastHeight = height;
            trimica$lastWidth = width;
            instance.info("Increased: {}x{}x{} {}-atlas", width, height, mipLevel, location);
        }
    }
}
