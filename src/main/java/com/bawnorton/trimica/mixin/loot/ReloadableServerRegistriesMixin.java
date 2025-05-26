package com.bawnorton.trimica.mixin.loot;

import com.bawnorton.trimica.Trimica;
import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.loot.LootTableReader;
import com.bawnorton.trimica.mixin.accessor.LootTable$BuilderAccessor;
import com.bawnorton.trimica.mixin.accessor.LootTableAccessor;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(ReloadableServerRegistries.class)
public abstract class ReloadableServerRegistriesMixin {
    @SuppressWarnings({"unchecked", "ConstantValue"})
    @ModifyReceiver(
            //? if fabric {
            method = "method_61240",
            //?} else {
            /*method = "lambda$scheduleRegistryLoad$5",
            *///?}
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"
            )
    )
    private static <T> Map<ResourceLocation, T> modifyLootTables(Map<ResourceLocation, T> instance, BiConsumer<ResourceLocation, T> consumer) {
        int counter = 0;
        for (Map.Entry<ResourceLocation, T> entry : instance.entrySet()) {
            T value = entry.getValue();
            if(!(value instanceof LootTable lootTable)) continue;

            List<Item> items = LootTableReader.read(rl -> (LootTable) instance.get(rl), lootTable);
            boolean containsTemplateItem = items.stream().anyMatch(item -> item instanceof SmithingTemplateItem);
            if(containsTemplateItem) {
                LootTable.Builder builder = new LootTable.Builder();
                LootTableAccessor accessor = (LootTableAccessor) lootTable;
                LootTable$BuilderAccessor builderAccessor = (LootTable$BuilderAccessor) builder;
                builder.setParamSet(lootTable.getParamSet());
                builderAccessor.trimica$pools(ImmutableList.<LootPool>builder().addAll(accessor.trimica$pools()));
                builderAccessor.trimica$functions(ImmutableList.<LootItemFunction>builder().addAll(accessor.trimica$functions()));
                accessor.trimica$randomSequence().ifPresent(builder::setRandomSequence);

                builder.withPool(LootPool.lootPool()
                                     .setRolls(ConstantValue.exactly(1))
                                     .add(LootItem.lootTableItem(TrimicaItems.ANIMATOR))
                                     .add(EmptyLootItem.emptyItem().setWeight(15)));
                instance.put(entry.getKey(), (T) builder.build());
                counter++;
            }
        }
        if(counter > 0) {
            Trimica.LOGGER.info("Added Trimica's Animator to {} loot tables", counter);
        }
        return instance;
    }
}
