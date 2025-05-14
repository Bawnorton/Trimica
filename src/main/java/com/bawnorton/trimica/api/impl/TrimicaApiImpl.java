package com.bawnorton.trimica.api.impl;

import com.bawnorton.trimica.api.BaseTextureInterceptor;
import com.bawnorton.trimica.api.CraftingRecipeInterceptor;
import com.bawnorton.trimica.api.TrimicaApi;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.PriorityQueue;
import java.util.Queue;

@SuppressWarnings("unused")
@ApiStatus.Internal
public final class TrimicaApiImpl implements TrimicaApi {
    public static final TrimicaApiImpl INSTANCE = new TrimicaApiImpl();

    private final Queue<SortableEndpointHolder<BaseTextureInterceptor>> baseTextureInterceptors = new PriorityQueue<>();
    private final Queue<SortableEndpointHolder<CraftingRecipeInterceptor>> craftingRecipeInterceptors = new PriorityQueue<>();

    public void registerBaseTextureInterceptor(int priority, BaseTextureInterceptor baseTextureInterceptor) {
        baseTextureInterceptors.add(new SortableEndpointHolder<>(baseTextureInterceptor, priority));
    }

    public void registerCraftingRecipeInterceptor(int priority, CraftingRecipeInterceptor craftingRecipeInterceptor) {
        craftingRecipeInterceptors.add(new SortableEndpointHolder<>(craftingRecipeInterceptor, priority));
    }

    public ResourceLocation applyBaseTextureInterceptorsForItem(ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
        for (SortableEndpointHolder<BaseTextureInterceptor> endpointHolder : baseTextureInterceptors) {
            expectedBaseTexture = endpointHolder.endpoint().interceptItemTexture(expectedBaseTexture, itemWithTrim, armourTrim);
        }
        return expectedBaseTexture;
    }

    public ResourceLocation applyBaseTextureInterceptorsForArmour(ResourceLocation expectedBaseTexture, ItemStack itemWithTrim, ArmorTrim armourTrim) {
        for (SortableEndpointHolder<BaseTextureInterceptor> endpointHolder : baseTextureInterceptors) {
            expectedBaseTexture = endpointHolder.endpoint().interceptArmourTexture(expectedBaseTexture, itemWithTrim, armourTrim);
        }
        return expectedBaseTexture;
    }

    public ResourceLocation applyBaseTextureInterceptorsForShield(ResourceLocation expectedBaseTexture, DataComponentGetter componentGetter, ArmorTrim armourTrim) {
        for (SortableEndpointHolder<BaseTextureInterceptor> endpointHolder : baseTextureInterceptors) {
            expectedBaseTexture = endpointHolder.endpoint().interceptShieldTexture(expectedBaseTexture, componentGetter, armourTrim);
        }
        return expectedBaseTexture;
    }

    public boolean applyCraftingRecipeInterceptorsForAddition(Item item) {
        for (SortableEndpointHolder<CraftingRecipeInterceptor> endpointHolder : craftingRecipeInterceptors) {
            TriState state = endpointHolder.endpoint().allowAsAddition(item);
            if (state == TriState.TRUE) {
                return true;
            } else if (state == TriState.FALSE) {
                return false;
            }
        }
        return true;
    }

    public boolean applyCraftingRecipeInterceptorsForBase(Item item) {
        for (SortableEndpointHolder<CraftingRecipeInterceptor> endpointHolder : craftingRecipeInterceptors) {
            TriState state = endpointHolder.endpoint().allowAsBase(item);
            if (state == TriState.TRUE) {
                return true;
            } else if (state == TriState.FALSE) {
                return false;
            }
        }
        return false;
    }

    private record SortableEndpointHolder<T>(T endpoint, int priority) implements Comparable<SortableEndpointHolder<T>> {
        @Override
        public int compareTo(@NotNull TrimicaApiImpl.SortableEndpointHolder<T> o) {
            return Integer.compare(this.priority, o.priority);
        }
    }
}
