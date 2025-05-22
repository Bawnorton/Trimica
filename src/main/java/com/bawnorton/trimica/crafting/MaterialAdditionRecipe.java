package com.bawnorton.trimica.crafting;

import com.bawnorton.trimica.api.impl.TrimicaApiImpl;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class MaterialAdditionRecipe implements SmithingRecipe {
    public static Serializer SERIALIZER;

    private final Ingredient base;
    private final Ingredient addition;
    private PlacementInfo placementInfo;

    public MaterialAdditionRecipe(Ingredient addition) {
        this.base = Ingredient.of(BuiltInRegistries.ITEM.stream().filter(TrimicaApiImpl.INSTANCE::applyCraftingRecipeInterceptorsForBase));
        this.addition = addition;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SmithingRecipeInput recipeInput, @NotNull HolderLookup.Provider provider) {
        return applyMaterialAddition(recipeInput.base(), recipeInput.addition());
    }

    public static @NotNull ItemStack applyMaterialAddition(ItemStack base, ItemStack addition) {
        ArmorTrim existing = base.get(DataComponents.TRIM);
        if(existing == null) return ItemStack.EMPTY;

        MaterialAdditions materialAdditions = base.getOrDefault(MaterialAdditions.TYPE, new MaterialAdditions(Set.of()));
        ResourceLocation additionKey = BuiltInRegistries.ITEM.getKey(addition.getItem());
        MaterialAdditions newMaterialAdditon = materialAdditions.and(additionKey);
        if(Objects.equals(materialAdditions, newMaterialAdditon)) return ItemStack.EMPTY;

        ItemStack result = base.copyWithCount(1);
        result.set(MaterialAdditions.TYPE, newMaterialAdditon);
        return result;
    }

    @Override
    public @NotNull RecipeSerializer<? extends SmithingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.createFromOptionals(List.of(Optional.empty(), Optional.of(this.base), Optional.of(this.addition)));
        }
        return placementInfo;
    }

    @Override
    public @NotNull Optional<Ingredient> templateIngredient() {
        return Optional.empty();
    }

    @Override
    public @NotNull Ingredient baseIngredient() {
        return base;
    }

    @Override
    public @NotNull Optional<Ingredient> additionIngredient() {
        return Optional.of(addition);
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        SlotDisplay baseSlot = base.display();
        SlotDisplay additionSlot = addition.display();
        return List.of(
                new Display(
                        baseSlot,
                        additionSlot,
                        new DemoSlotDisplay(baseSlot, additionSlot),
                        new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)
                )
        );
    }

    private Ingredient getAddition() {
        return addition;
    }

    public static class Serializer implements RecipeSerializer<MaterialAdditionRecipe> {
        private static final MapCodec<MaterialAdditionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("addition").forGetter(MaterialAdditionRecipe::getAddition)
        ).apply(instance, MaterialAdditionRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MaterialAdditionRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                MaterialAdditionRecipe::getAddition,
                MaterialAdditionRecipe::new
        );

        @Override
        public @NotNull MapCodec<MaterialAdditionRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MaterialAdditionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public record Display(SlotDisplay base, SlotDisplay addition, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
        public static final MapCodec<Display> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                            SlotDisplay.CODEC.fieldOf("base").forGetter(Display::base),
                                            SlotDisplay.CODEC.fieldOf("addition").forGetter(Display::addition),
                                            SlotDisplay.CODEC.fieldOf("result").forGetter(Display::result),
                                            SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(Display::craftingStation)
                                    )
                                    .apply(instance, Display::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Display> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC,
                Display::base,
                SlotDisplay.STREAM_CODEC,
                Display::addition,
                SlotDisplay.STREAM_CODEC,
                Display::result,
                SlotDisplay.STREAM_CODEC,
                Display::craftingStation,
                Display::new
        );
        public static final RecipeDisplay.Type<Display> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public @NotNull Type<? extends RecipeDisplay> type() {
            return TYPE;
        }
    }

    public record DemoSlotDisplay(SlotDisplay base, SlotDisplay material) implements SlotDisplay {
        public static final MapCodec<DemoSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                            SlotDisplay.CODEC.fieldOf("base").forGetter(DemoSlotDisplay::base),
                                            SlotDisplay.CODEC.fieldOf("material").forGetter(DemoSlotDisplay::material)
                                    )
                                    .apply(instance, DemoSlotDisplay::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, DemoSlotDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC,
                DemoSlotDisplay::base,
                SlotDisplay.STREAM_CODEC,
                DemoSlotDisplay::material,
                DemoSlotDisplay::new
        );
        public static final Type<DemoSlotDisplay> TYPE = new SlotDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

        @Override
        public <T> @NotNull Stream<T> resolve(@NotNull ContextMap contextMap, @NotNull DisplayContentsFactory<T> displayContentsFactory) {
            if (!(displayContentsFactory instanceof DisplayContentsFactory.ForStacks<T> forStacks)) return Stream.empty();

            HolderLookup.Provider provider = contextMap.getOptional(SlotDisplayContext.REGISTRIES);
            if (provider == null) return Stream.empty();

            RandomSource randomSource = RandomSource.create(System.identityHashCode(this));
            List<ItemStack> list = this.base.resolveForStacks(contextMap);
            if (list.isEmpty()) return Stream.empty();

            List<ItemStack> list2 = this.material.resolveForStacks(contextMap);
            if (list2.isEmpty()) return Stream.empty();

            return Stream.generate(() -> {
                ItemStack itemStack = Util.getRandom(list, randomSource);
                ItemStack itemStack2 = Util.getRandom(list2, randomSource);
                return MaterialAdditionRecipe.applyMaterialAddition(itemStack, itemStack2);
            }).limit(256L).filter(itemStack -> !itemStack.isEmpty()).limit(16L).map(forStacks::forStack);
        }

        @Override
        public @NotNull Type<? extends SlotDisplay> type() {
            return TYPE;
        }
    }
}
