package com.bawnorton.trimica.platform.fabric.data.provider;

import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.tags.TrimicaTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TrimicaAdvancementsProvider extends FabricAdvancementProvider {
    public TrimicaAdvancementsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        Advancement.Builder.advancement()
                           .addCriterion(
                                   "material_addition",
                                   RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                           ResourceKey.create(
                                                   Registries.RECIPE,
                                                   TrimicaTags.MATERIAL_ADDITIONS.location()
                                           )
                                   )
                           )
                           .parent(new AdvancementHolder(ResourceLocation.withDefaultNamespace("adventure/trim_with_any_armor_pattern"), null))
                           .display(
                                   new ItemStack(TrimicaItems.ANIMATOR),
                                   Component.translatable("trimica.advancements.adventure.add_material_addition.title"),
                                   Component.translatable("trimica.advancements.adventure.add_material_addition.description"),
                                   null,
                                   AdvancementType.TASK,
                                   true,
                                   true,
                                   false
                           )
                           .save(consumer, "trimica:adventure/add_material_addition");

        Advancement.Builder builder = Advancement.Builder.advancement();
        VanillaRecipeProvider.smithingTrims()
                             .map(VanillaRecipeProvider.TrimTemplate::recipeId)
                             .forEach(resourceKey -> builder.addCriterion(
                                     "armor_trimmed_with_rainbowifier_" + resourceKey.location(),
                                     RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                             resourceKey,
                                             List.of(
                                                     ItemPredicate.Builder.item()
                                                                          .of(registryLookup.lookupOrThrow(Registries.ITEM), TrimicaItems.RAINBOWIFIER)
                                             )
                                     )
                             ));
        builder.requirements(AdvancementRequirements.Strategy.OR)
               .parent(new AdvancementHolder(ResourceLocation.withDefaultNamespace("adventure/trim_with_any_armor_pattern"), null))
               .display(
                       new ItemStack(TrimicaItems.RAINBOWIFIER),
                       Component.translatable("trimica.advancements.adventure.add_rainbowifier_material.title"),
                       Component.translatable("trimica.advancements.adventure.add_rainbowifier_material.description"),
                       null,
                       AdvancementType.TASK,
                       true,
                       true,
                       true
               )
               .save(consumer, "trimica:adventure/add_rainbowifier_material");
    }
}
