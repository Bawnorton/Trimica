package com.bawnorton.trimica.api.impl;

import com.bawnorton.trimica.api.BaseTextureInterceptor;
import com.bawnorton.trimica.api.TrimicaApi;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.ApiStatus;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@ApiStatus.Internal
public final class TrimicaApiImpl implements TrimicaApi {
    public static final TrimicaApiImpl INSTANCE = new TrimicaApiImpl();

    private final List<BaseTextureInterceptor> baseTextureInterceptors = new ArrayList<>();

    public void registerBaseTextureInterceptor(BaseTextureInterceptor baseTextureInterceptor) {
        baseTextureInterceptors.add(baseTextureInterceptor);
    }

    public ResourceLocation applyBaseTextureInterceptorsForItem(ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
        for (BaseTextureInterceptor intercepter : baseTextureInterceptors) {
            expectedBaseTexture = intercepter.interceptItemTexture(expectedBaseTexture, itemWithTrim, armourTrim);
        }
        return expectedBaseTexture;
    }

    public ResourceLocation applyBaseTextureInterceptorsForArmour(ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
        for (BaseTextureInterceptor intercepter : baseTextureInterceptors) {
            expectedBaseTexture = intercepter.interceptArmourTexture(expectedBaseTexture, itemWithTrim, armourTrim);
        }
        return expectedBaseTexture;
    }

    public ResourceLocation applyBaseTextureInterceptorsForShield(ResourceLocation expectedBaseTexture, DataComponentGetter componentGetter, ArmorTrim armourTrim) {
        for (BaseTextureInterceptor intercepter : baseTextureInterceptors) {
            expectedBaseTexture = intercepter.interceptShieldTexture(expectedBaseTexture, componentGetter, armourTrim);
        }
        return expectedBaseTexture;
    }
}
