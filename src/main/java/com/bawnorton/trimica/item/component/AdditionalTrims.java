package com.bawnorton.trimica.item.component;

import com.bawnorton.configurable.Configurable;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import java.util.ArrayList;
import java.util.List;

public record AdditionalTrims(List<ArmorTrim> trims) {
    public static DataComponentType<AdditionalTrims> TYPE;

    public static final Codec<AdditionalTrims> CODEC = ArmorTrim.CODEC.listOf().xmap(
            list -> new AdditionalTrims(List.copyOf(list)),
            additionalTrims -> additionalTrims == null ? List.of() : List.copyOf(additionalTrims.trims)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AdditionalTrims> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, ArmorTrim>list().apply(ArmorTrim.STREAM_CODEC).map(
            list -> new AdditionalTrims(List.copyOf(list)),
            additionalTrims -> additionalTrims == null ? List.of() : List.copyOf(additionalTrims.trims)
    );

    /**
     * Whether you can add trims to already trimmed armor.
     */
    @Configurable(onSet = "com.bawnorton.trimica.Trimica#refreshEverything")
    public static boolean enableAdditionalTrims = false;

    public AdditionalTrims(ArmorTrim... trims) {
        this(List.of(trims));
    }

    public static AdditionalTrims empty() {
        return new AdditionalTrims();
    }

    public boolean isEmpty() {
        return trims.isEmpty();
    }

    public List<ArmorTrim> mutableTrims() {
        return new ArrayList<>(trims);
    }

    public static List<ArmorTrim> getAllTrims(DataComponentGetter getter) {
        if (getter == null) return List.of();

        if (!enableAdditionalTrims) {
            ArmorTrim normalTrim = getter.get(DataComponents.TRIM);
            if (normalTrim != null) {
                return List.of(normalTrim);
            }
            return List.of();
        }

        AdditionalTrims additionalTrims = getter.getOrDefault(AdditionalTrims.TYPE, AdditionalTrims.empty());
        List<ArmorTrim> allTrims = additionalTrims.mutableTrims();

        ArmorTrim normalTrim = getter.get(DataComponents.TRIM);
        if (normalTrim != null) {
            allTrims.addFirst(normalTrim);
        }
        return allTrims;
    }

    public static boolean tryAddTrim(ItemStack stack, ArmorTrim trim) {
        if (!stack.has(DataComponents.TRIM)) {
            stack.set(DataComponents.TRIM, trim);
            return true;
        }

        if (!enableAdditionalTrims) return false;

        AdditionalTrims additionalTrims = stack.getOrDefault(AdditionalTrims.TYPE, AdditionalTrims.empty());
        if (additionalTrims.trims().contains(trim)) return false;

        List<ArmorTrim> newTrims = additionalTrims.mutableTrims();
        newTrims.add(trim);
        stack.set(AdditionalTrims.TYPE, new AdditionalTrims(newTrims));
        return true;
    }

    public static int removeTrims(ItemStack stack, int count) {
        if (!enableAdditionalTrims) {
            if (count <= 0 || !stack.has(DataComponents.TRIM)) return count;

            stack.remove(DataComponents.TRIM);
            if (MaterialAdditions.enableMaterialAdditions) {
                stack.remove(MaterialAdditions.TYPE);
            }
            return count - 1;
        }

        AdditionalTrims additionalTrims = stack.getOrDefault(AdditionalTrims.TYPE, AdditionalTrims.empty());
        if(count <= 0 || (additionalTrims.isEmpty() && !stack.has(DataComponents.TRIM))) return 0;

        List<ArmorTrim> newTrims = additionalTrims.mutableTrims();
        for (; count > 0 && !newTrims.isEmpty(); count--) {
            newTrims.removeLast();
        }
        if (count > 0 && stack.has(DataComponents.TRIM)) {
            stack.remove(DataComponents.TRIM);
            count--;
        }
        if (newTrims.isEmpty()) {
            stack.remove(AdditionalTrims.TYPE);
            if (!stack.has(DataComponents.TRIM) && MaterialAdditions.enableMaterialAdditions) {
                stack.remove(MaterialAdditions.TYPE);
            }
        } else {
            stack.set(AdditionalTrims.TYPE, new AdditionalTrims(newTrims));
        }
        return count; // remainder
    }

    public static void correctTrimComponents(ItemStack stack) {
        if (!enableAdditionalTrims) return;

        if (!stack.has(AdditionalTrims.TYPE) || stack.has(DataComponents.TRIM)) return;

        AdditionalTrims additionalTrims = stack.getOrDefault(AdditionalTrims.TYPE, AdditionalTrims.empty());
        if (additionalTrims.isEmpty()) return;

        List<ArmorTrim> trims = additionalTrims.mutableTrims();
        ArmorTrim firstTrim = trims.removeFirst();
        stack.set(DataComponents.TRIM, firstTrim);
        if (trims.isEmpty()) {
            stack.remove(AdditionalTrims.TYPE);
        } else {
            stack.set(AdditionalTrims.TYPE, new AdditionalTrims(trims));
        }
    }

    public static boolean hasTrim(ItemStack stack, ArmorTrim trim) {
        List<ArmorTrim> allTrims = getAllTrims(stack);
        for (ArmorTrim armorTrim : allTrims) {
            if (armorTrim.equals(trim)) {
                return true;
            }
        }
        return false;
    }
}
