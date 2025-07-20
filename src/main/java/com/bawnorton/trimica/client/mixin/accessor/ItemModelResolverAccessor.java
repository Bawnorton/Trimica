package com.bawnorton.trimica.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.function.Function;

@MixinEnvironment(value = "client")
@Mixin(ItemModelResolver.class)
public interface ItemModelResolverAccessor {
    @Accessor("modelGetter")
    Function<ResourceLocation, ItemModel> trimica$modelGetter();
}
