package com.bawnorton.trimica.component;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;

public class RuntimeProvidesTrimMaterial extends ProvidesTrimMaterial {
    public RuntimeProvidesTrimMaterial(ItemStack stack) {
        super(Holder.direct(Trimica.MATERIAL_REGISTRY.getOrCreate(stack)));
    }
}
