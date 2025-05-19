package com.bawnorton.trimica.item;

import com.bawnorton.trimica.Trimica;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemLore;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class TrimicaItems {
    private static final List<ItemHolder> ITEMS = new ArrayList<>();

    public static final Item RAINBOW_MATERIAL = create("rainbow_material", Item::new, new Item.Properties()
            .rarity(Rarity.UNCOMMON)
            .component(DataComponents.LORE,  new ItemLore(
                    List.of(
                            Component.translatable("item.trimica.rainbow_material.description")
                                    .withStyle(style -> style.withItalic(false)
                                            .withColor(ChatFormatting.GREEN)
                                    )
                    )
            ))
            .trimMaterial(ResourceKey.create(Registries.TRIM_MATERIAL, Trimica.rl("rainbow")))
    );

    public static final Item ANIMATOR_MATERIAL = create("animator_material", Item::new, new Item.Properties()
            .rarity(Rarity.UNCOMMON)
            .component(DataComponents.LORE,  new ItemLore(
                    List.of(
                            Component.translatable("item.trimica.animator_material.description")
                                    .withStyle(style -> style.withItalic(false)
                                            .withColor(ChatFormatting.GREEN)
                                    )
                    )
            ))
    );

    public static void forEach(BiConsumer<ResourceKey<Item>, Item> consumer) {
        for (ItemHolder itemHolder : ITEMS) {
            consumer.accept(itemHolder.key(), itemHolder.item());
        }
    }

    private static Item create(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Trimica.rl(name));
        properties.setId(key);
        Item item = factory.apply(properties);
        ITEMS.add(new ItemHolder(key, item));
        return item;
    }

    private record ItemHolder(ResourceKey<Item> key, Item item) {
    }
}
