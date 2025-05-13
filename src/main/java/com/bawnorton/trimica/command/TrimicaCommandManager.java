package com.bawnorton.trimica.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import java.util.List;
import java.util.stream.Stream;

public class TrimicaCommandManager {
    public static void init(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("trimica")
                                    .then(Commands.literal("debug")
                                                  .requires(source -> source.hasPermission(4))
                                                  .then(Commands.literal("summon")
                                                                .executes(TrimicaCommandManager::summonArmourStands)
                                                  )
                                    )
        );
    }

    private static int summonArmourStands(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        RegistryAccess registryAccess = source.registryAccess();
        HolderLookup.RegistryLookup<TrimPattern> patternLookup = registryAccess.lookupOrThrow(Registries.TRIM_PATTERN);
        HolderLookup.RegistryLookup<TrimMaterial> materialLookup = registryAccess.lookupOrThrow(Registries.TRIM_MATERIAL);
        ServerLevel level = source.getLevel();
        BlockPos.MutableBlockPos pos = BlockPos.containing(source.getPosition()).mutable();
        source.sendSystemMessage(Component.literal("Summoning %s armour stands at %s".formatted(
                patternLookup.listElementIds().count() * materialLookup.listElementIds().count(), pos.toShortString()
        )));
        patternLookup.listElements()
                     .forEach(patternRef -> {
                         materialLookup.listElements().forEach(materialRef -> {
                             ArmorTrim trim = new ArmorTrim(materialRef, patternRef);
                             List<ItemStack> toEquip = Stream.of(
                                     Items.DIAMOND_HELMET,
                                     Items.DIAMOND_CHESTPLATE,
                                     Items.DIAMOND_LEGGINGS,
                                     Items.DIAMOND_BOOTS,
                                     Items.SHIELD
                             ).map(item -> {
                                 ItemStack stack = item.getDefaultInstance();
                                 stack.set(DataComponents.TRIM, trim);
                                 return stack;
                             }).toList();
                             ArmorStand stand = EntityType.ARMOR_STAND.create(
                                     level, armourStand -> {
                                         armourStand.setNoGravity(true);
                                         armourStand.setNoBasePlate(true);
                                         armourStand.setShowArms(true);
                                         armourStand.absSnapRotationTo(0, 0);
                                         toEquip.forEach(stack -> {
                                             Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
                                             assert equippable != null;

                                             armourStand.setItemSlot(equippable.slot(), stack);
                                         });
                                     }, pos, EntitySpawnReason.MOB_SUMMONED, false, false
                             );
                             level.addFreshEntity(stand);
                             pos.move(Direction.EAST, 2);
                         });
                         pos.move(Direction.WEST, (int) (2 * materialLookup.listElementIds().count()));
                         pos.move(Direction.NORTH, 2);
                     });
        return 1;
    }
}
