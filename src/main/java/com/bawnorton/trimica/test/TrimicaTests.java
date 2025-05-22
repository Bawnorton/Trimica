package com.bawnorton.trimica.test;

import com.bawnorton.trimica.item.TrimicaItems;
import com.bawnorton.trimica.item.component.ComponentUtil;
import com.bawnorton.trimica.item.component.MaterialAdditions;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class TrimicaTests {
    private static final AtomicReference<ServerPlayer> playerRef = new AtomicReference<>();

    public static void runTests(ClientGameTestContext context) {
        context.getInput().resizeWindow(1024, 512);
        context.runOnClient(client -> {
            client.options.renderDistance().set(2);
            client.options.simulationDistance().set(5);
        });

        try(TestSingleplayerContext singleplayerContext = context.worldBuilder().create()) {
            TestServerContext serverContext = singleplayerContext.getServer();
            setupPlayer(serverContext, singleplayerContext);
            // validate builtin vanilla trim
            applyTrimAndValidate(context, serverContext, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.IRON_HELMET, Items.DIAMOND, getValidatorFor(
                    "diamond",
                    ResourceLocation.withDefaultNamespace("silence")
            ));
            // validate builtin shield trim
            applyTrimAndValidate(context, serverContext, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SHIELD, Items.REDSTONE, getValidatorFor(
                    "redstone",
                    ResourceLocation.withDefaultNamespace("flow")
            ));
            // validate custom vanilla trim
            applyTrimAndValidate(context, serverContext, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.DIAMOND_CHESTPLATE, Items.END_CRYSTAL, getValidatorFor(
                    "trimica/%s".formatted(BuiltInRegistries.ITEM.getKey(Items.END_CRYSTAL).toString().replace(":", "-")),
                    ResourceLocation.withDefaultNamespace("bolt")
            ));
            // validate custom shield trim
            applyTrimAndValidate(context, serverContext, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SHIELD, Items.ENDER_EYE, getValidatorFor(
                    "trimica/%s".formatted(BuiltInRegistries.ITEM.getKey(Items.ENDER_EYE).toString().replace(":", "-")),
                    ResourceLocation.withDefaultNamespace("ward")
            ));
            // validate rainbow trim
            applyTrimAndValidate(context, serverContext, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TURTLE_HELMET, TrimicaItems.RAINBOWIFIER, getValidatorFor(
                    "rainbow",
                    ResourceLocation.withDefaultNamespace("eye")
            ));

            // validate material addition
            applyTrimMaterialAdditionAndValidate(context, serverContext, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.GOLDEN_CHESTPLATE, Items.SPYGLASS, TrimicaItems.ANIMATOR, addition -> {
                ResourceLocation expectedKey = BuiltInRegistries.ITEM.getKey(TrimicaItems.ANIMATOR);
                if (!addition.matches(expectedKey)) {
                    throw new AssertionError("Expected material addition %s to be present, found %s".formatted(expectedKey, addition.additionKeys()));
                }
            });

            // validate material addition
            applyTrimMaterialAdditionAndValidate(context, serverContext, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.LEATHER_LEGGINGS, Items.ENDER_EYE, Items.GLOW_INK_SAC, addition -> {
                ResourceLocation expectedKey = BuiltInRegistries.ITEM.getKey(Items.GLOW_INK_SAC);
                if (!addition.matches(expectedKey)) {
                    throw new AssertionError("Expected material addition %s to be present, found %s".formatted(expectedKey, addition.additionKeys()));
                }
            });

            context.runOnClient(client -> client.options.hideGui = true);

            Holder<TrimPattern> silencePattern = serverContext.computeOnServer(server -> {
                Registry<TrimPattern> trimPatterns = server.registryAccess().lookup(Registries.TRIM_PATTERN).orElseThrow();
                return trimPatterns.getOrThrow(TrimPatterns.SILENCE);
            });

            Holder<TrimMaterial> goldMaterial = serverContext.computeOnServer(server -> {
                Registry<TrimMaterial> trimMaterials = server.registryAccess().lookup(Registries.TRIM_MATERIAL).orElseThrow();
                return trimMaterials.getOrThrow(TrimMaterials.GOLD);
            });

            // validate rendering of builtin trim
            createTrimmedArmourStand(context, serverContext, goldMaterial, silencePattern, () -> context.assertScreenshotEquals("gold_silence_armour_stand"));

            Holder<TrimMaterial> enderPearlMaterial = serverContext.computeOnServer(server -> {
                ItemStack enderPearl = Items.ENDER_PEARL.getDefaultInstance();
                ProvidesTrimMaterial trimMaterialProvider = enderPearl.get(DataComponents.PROVIDES_TRIM_MATERIAL);
                if (trimMaterialProvider == null) {
                    throw new AssertionError("Expected trim material provider, got null");
                }
                return trimMaterialProvider.material().contents().left().orElseThrow();
            });

            // validate rendering of custom trim
            createTrimmedArmourStand(context, serverContext, enderPearlMaterial, silencePattern, () -> context.assertScreenshotEquals("ender_pearl_silence_armour_stand"));
        }
    }

    private static void createTrimmedArmourStand(
            ClientGameTestContext context,
            TestServerContext serverContext,
            Holder<TrimMaterial> material,
            Holder<TrimPattern> pattern,
            Runnable validator) {
        ArmorStand spawned = serverContext.computeOnServer(server -> {
            ArmorTrim trim = new ArmorTrim(material, pattern);
            List<ItemStack> toEquip = ComponentUtil.getTrimmedEquipment(trim);
            BlockPos pos = new BlockPos(0, -60, 4);
            ServerLevel level = server.overworld();
            ArmorStand stand = EntityType.ARMOR_STAND.create(
                    level, armourStand -> {
                        armourStand.setNoGravity(true);
                        armourStand.setNoBasePlate(true);
                        armourStand.setShowArms(true);
                        toEquip.forEach(stack -> {
                            Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
                            assert equippable != null;

                            armourStand.setItemSlot(equippable.slot(), stack);
                        });
                    }, pos, EntitySpawnReason.MOB_SUMMONED, false, false);
            if (stand != null) {
                stand.lookAt(EntityAnchorArgument.Anchor.FEET, new Vec3(3.5, -60, 0.5));
                level.addFreshEntity(stand);
            }
            return stand;
        });
        context.waitTick();
        validator.run();
        serverContext.runOnServer(server -> spawned.kill(server.overworld()));
        context.waitTick();
    }

    private static void setupPlayer(TestServerContext serverContext, TestSingleplayerContext singleplayerContext) {
        serverContext.runCommand("/tp @a 0 -60 0");
        serverContext.runOnServer(server -> {
            ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
            playerRef.set(player);
        });
        singleplayerContext.getClientWorld().waitForChunksRender();
    }
    private static void applyTrimAndValidate(
            ClientGameTestContext context,
            TestServerContext serverContext,
            Item template,
            Item trimmable,
            Item addition,
            BiConsumer<String, ResourceLocation> validator) {
        applyTrimAndValidate(context, serverContext, template, trimmable, addition, true, validator);
    }

    private static void applyTrimAndValidate(
            ClientGameTestContext context,
            TestServerContext serverContext,
            Item template,
            Item trimmable,
            Item addition,
            boolean clear,
            BiConsumer<String, ResourceLocation> validator) {
        placeAndOpenSmithingTable(context, serverContext);
        serverContext.runOnServer(server -> {
            ServerPlayer player = playerRef.get();
            player.addItem(template.getDefaultInstance());
            player.addItem(trimmable.getDefaultInstance());
            player.addItem(addition.getDefaultInstance());
            AbstractContainerMenu menu = player.containerMenu;
            if(!(menu instanceof SmithingMenu smithingMenu)) {
                throw new AssertionError("Expected SmithingMenu, got " + menu);
            }
            smithingMenu.quickMoveStack(player, 31); // Template
            smithingMenu.quickMoveStack(player, 32); // Trimmable
            smithingMenu.quickMoveStack(player, 33); // Addition
            smithingMenu.quickMoveStack(player, 3);  // Smithing Menu Output

            player.closeContainer();
        });
        context.waitTick(); // ensure the menu is closed
        serverContext.runOnServer(server -> {
            ServerPlayer player = playerRef.get();
            InventoryMenu inventoryMenu = player.inventoryMenu;
            ItemStack result = inventoryMenu.getSlot(44).getItem(); // Rightmost slot in the hotbar
            ArmorTrim trim = result.get(DataComponents.TRIM);
            if(trim == null) {
                throw new AssertionError("Expected trim, got null");
            }
            Equippable equippable = trimmable.components().get(DataComponents.EQUIPPABLE);
            if(equippable == null) {
                throw new AssertionError("Expected trimmable to be equippable");
            }
            ResourceKey<EquipmentAsset> assetResourceKey = equippable.assetId().orElse(null);
            MaterialAssetGroup materialAssetGroup = trim.material().value().assets();
            String suffix = assetResourceKey == null ? materialAssetGroup.base().suffix() : materialAssetGroup.assetId(assetResourceKey).suffix();
            ResourceLocation patternKey = trim.pattern().value().assetId();

            validator.accept(suffix, patternKey);
            if (clear) {
                player.getInventory().clearContent();
            }
        });
    }

    private static void placeAndOpenSmithingTable(ClientGameTestContext context, TestServerContext serverContext) {
        serverContext.runOnServer(server -> {
            BlockPos tablePos = new BlockPos(0, -60, 1);
            ServerLevel level = server.overworld();
            level.setBlock(tablePos, Blocks.SMITHING_TABLE.defaultBlockState(), 0);
            ServerPlayer player = playerRef.get();
            player.openMenu(Blocks.SMITHING_TABLE.defaultBlockState().getMenuProvider(level, tablePos));
        });
        context.waitTick();
    }

    private static @NotNull BiConsumer<String, ResourceLocation> getValidatorFor(String expectedSuffix, ResourceLocation expectedPattern) {
        return (material, pattern) -> {
            if (!material.equals(expectedSuffix)) {
                throw new AssertionError("Expected material %s, got %s".formatted(expectedSuffix, material));
            }
            if (!pattern.equals(expectedPattern)) {
                throw new AssertionError("Expected pattern %s, got %s".formatted(expectedPattern, pattern));
            }
        };
    }

    private static void applyTrimMaterialAdditionAndValidate(
            ClientGameTestContext context,
            TestServerContext serverContext,
            Item template,
            Item trimmable,
            Item addition,
            Item materialAddition,
            Consumer<MaterialAdditions> validator) {
        applyTrimAndValidate(context, serverContext, template, trimmable, addition, false, (material, pattern) -> {});
        placeAndOpenSmithingTable(context, serverContext);
        serverContext.runOnServer(server -> {
            ServerPlayer player = playerRef.get();
            player.addItem(materialAddition.getDefaultInstance());
            AbstractContainerMenu menu = player.containerMenu;
            if(!(menu instanceof SmithingMenu smithingMenu)) {
                throw new AssertionError("Expected SmithingMenu, got " + menu);
            }
            smithingMenu.quickMoveStack(player, 39); // Result
            smithingMenu.quickMoveStack(player, 31); // Addition
            smithingMenu.quickMoveStack(player, 3);  // Smithing Menu Output

            player.closeContainer();
        });
        context.waitTick();
        serverContext.runOnServer(server -> {
            ServerPlayer player = playerRef.get();
            InventoryMenu inventoryMenu = player.inventoryMenu;
            ItemStack result = inventoryMenu.getSlot(44).getItem(); // Rightmost slot in the hotbar
            MaterialAdditions materialAdditionsComponent = result.get(MaterialAdditions.TYPE);
            if (materialAdditionsComponent == null) {
                throw new AssertionError("Expected material addition, got null");
            }
            validator.accept(materialAdditionsComponent);
        });
    }
}
