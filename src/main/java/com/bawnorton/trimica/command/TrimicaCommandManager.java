package com.bawnorton.trimica.command;

import com.bawnorton.trimica.item.component.ComponentUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Rotations;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import java.util.List;
import java.util.Objects;

public class TrimicaCommandManager {
    public static void init(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("trimica")
                                    .then(Commands.literal("debug")
                                                  .requires(source -> source.hasPermission(4))
                                                  .then(Commands.literal("summon")
                                                                .then(Commands.literal("all")
                                                                              .executes(context -> {
                                                                                  summonArmourStands(context, true);
                                                                                  return 1;
                                                                              }))
                                                                .executes(context -> summonArmourStands(context, false))
                                                  )
                                    )
        );
    }

    private static int summonArmourStands(CommandContext<CommandSourceStack> context, boolean allMaterials) {
        CommandSourceStack source = context.getSource();
        RegistryAccess registryAccess = source.registryAccess();
        HolderLookup.RegistryLookup<TrimPattern> patternLookup = registryAccess.lookupOrThrow(Registries.TRIM_PATTERN);
        ServerLevel level = source.getLevel();
        BlockPos.MutableBlockPos pos = BlockPos.containing(source.getPosition()).mutable();
        List<? extends Holder<TrimMaterial>> materials = allMaterials
                ? registryAccess.lookupOrThrow(Registries.ITEM).listElements().map(itemRef -> {
            Item item = itemRef.value();
            ProvidesTrimMaterial trimMaterialProvider = item.getDefaultInstance().get(DataComponents.PROVIDES_TRIM_MATERIAL);
            if (trimMaterialProvider == null) return null;

            return trimMaterialProvider.material().unwrap(registryAccess).orElse(null);
        }).filter(Objects::nonNull).toList()
                : registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL).listElements().toList();
        source.sendSystemMessage(Component.literal("Summoning %s armour stands at %s".formatted(
                patternLookup.listElementIds().count() * materials.size(), pos.toShortString()
        )));
        var ref = new Object() {
            boolean flag = false;
        };
        patternLookup.listElements()
                     .forEach(patternRef -> {
                         materials.forEach(materialRef -> {
                             List<ItemStack> toEquip = ComponentUtil.getTrimmedEquipment(new ArmorTrim(materialRef, patternRef));
                             ArmorStand stand = EntityType.ARMOR_STAND.create(
                                     level, armourStand -> {
                                         armourStand.setNoGravity(true);
                                         armourStand.setNoBasePlate(true);
                                         armourStand.setShowArms(true);
                                         armourStand.absSnapRotationTo(0, 0);
                                         armourStand.setLeftArmPose(new Rotations(-70, 80, -20));
                                         toEquip.forEach(stack -> {
                                             Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
                                             assert equippable != null;

                                             armourStand.setItemSlot(equippable.slot(), stack);
                                         });
                                     }, pos, EntitySpawnReason.MOB_SUMMONED, false, false
                             );
                             if (stand != null) {
                                 level.addFreshEntity(stand);
                             }
                             pos.move(Direction.EAST, 2);
                         });
                         pos.move(Direction.WEST, 2 * materials.size());
                         pos.move(Direction.UP);
                         //noinspection AssignmentUsedAsCondition - Deliberate
                         if (ref.flag = !ref.flag) {
                             pos.move(Direction.WEST);
                         } else {
                             pos.move(Direction.EAST);
                         }
                         pos.move(Direction.NORTH);
                     });
        return 1;
    }
}
