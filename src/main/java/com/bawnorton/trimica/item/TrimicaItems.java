package com.bawnorton.trimica.item;

import com.bawnorton.trimica.Trimica;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class TrimicaItems {
    private static final List<ItemHolder> ITEMS = new ArrayList<>();

    public static final Item RAINBOWIFIER = create("rainbowifier", Item::new, new Item.Properties()
            .rarity(Rarity.UNCOMMON)
            .trimMaterial(ResourceKey.create(Registries.TRIM_MATERIAL, Trimica.rl("rainbow")))
    );

    public static final Item ANIMATOR = create("animator", Item::new, new Item.Properties()
            .rarity(Rarity.UNCOMMON)
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
