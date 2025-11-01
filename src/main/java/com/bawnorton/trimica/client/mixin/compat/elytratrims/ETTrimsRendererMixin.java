/*
package com.bawnorton.trimica.client.mixin.compat.elytratrims;

import com.bawnorton.trimica.client.TrimicaClient;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlas;
import com.bawnorton.trimica.client.texture.RuntimeTrimAtlases;
import com.bawnorton.trimica.client.texture.TrimArmourSpriteFactory;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.elytratrims.Memoizer;
import dev.kikugie.elytratrims.render.ETRenderParameters;
import dev.kikugie.elytratrims.render.ETTrimsRenderer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.type.trim.ArmorTrim;
import net.minecraft.world.item.type.trim.TrimMaterial;
import net.minecraft.world.item.type.trim.TrimPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@MixinEnvironment(value = "client")
@Mixin(ETTrimsRenderer.class)
public abstract class ETTrimsRendererMixin {
    @Shadow protected abstract ResourceLocation texture(ArmorTrim trim);

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/kikugie/elytratrims/Memoizer;invoke(Ljava/lang/Object;)Ljava/lang/Object;"
            ),
            remap = false
    )
    private Object provideRuntimeTrimSprites(Memoizer<ArmorTrim, TextureAtlasSprite> instance, Object o, Operation<Object> original, ETRenderParameters parameters) {
        Object sprite = original.call(instance, o);
        ArmorTrim trim = parameters.getStack().get(DataComponents.TRIM);
        if (trim == null) return sprite;

        TrimMaterial material = trim.material().value();
        ItemStack stack = TrimArmourSpriteFactory.ITEM_WITH_TRIM_CAPTURE.get();
        if(stack == null) return sprite;

        MaterialAdditions addition = stack.getOrDefault(MaterialAdditions.TYPE, MaterialAdditions.NONE);
        if (sprite != null && addition.isEmpty()) return sprite;

        RuntimeTrimAtlases atlases = TrimicaClient.getRuntimeAtlases();
        TrimPattern pattern = trim.pattern().value();
        RuntimeTrimAtlas atlas = atlases.getEquipmentAtlas(pattern, EquipmentClientInfo.LayerType.WINGS);
        if (atlas == null) return sprite;

        ResourceLocation overlayLocation = texture(trim);
        if (addition != null) {
            overlayLocation = addition.apply(overlayLocation);
        }
        return atlas.getSprite(stack, material, overlayLocation);
    }
}
*/
