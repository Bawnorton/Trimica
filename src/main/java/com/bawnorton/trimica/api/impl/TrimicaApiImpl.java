package com.bawnorton.trimica.api.impl;

import com.bawnorton.trimica.api.BaseTextureInterceptor;
import com.bawnorton.trimica.api.TrimicaApi;
import com.bawnorton.trimica.api.TrimmedType;
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

    public ResourceLocation applyBaseTextureIntercepters(ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim, TrimmedType trimmedType) {
        for (BaseTextureInterceptor intercepter : baseTextureInterceptors) {
            expectedBaseTexture = intercepter.intercept(expectedBaseTexture, itemWithTrim, armourTrim, trimmedType);
        }
        return expectedBaseTexture;
    }
}
