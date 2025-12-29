package com.mas6y6.masworld.Items.Attributes;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class WeaponCooldown {
    public Masworld main;

    public WeaponCooldown(Masworld plugin) {
        this.main = plugin;
    }

    public int change(CommandContext<CommandSourceStack> context) {
        Double value = context.getArgument("value",Double.class);

        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.admin"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        NamespacedKey namespace = new NamespacedKey(this.main, "weapon_cooldown");

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.ATTACK_DAMAGE);

            if (modifiers != null) {
                modifiers.stream()
                        .filter(mod -> mod.getName().equals(namespace.getKey()))
                        .forEach(mod -> meta.removeAttributeModifier(Attribute.ATTACK_DAMAGE, mod));
            }
        });

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            meta.addAttributeModifier(
                    Attribute.ATTACK_SPEED,
                    new AttributeModifier(
                            namespace,
                            value,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.HAND
                    )
            );
        });

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully added \"masworld:dynamite_power\" = \""+ value +"\".").color(NamedTextColor.GREEN)));

        return 0;
    }

    public int reset(CommandContext<CommandSourceStack> context) {

        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.admin"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            meta.removeAttributeModifier(Attribute.ATTACK_SPEED);
        });

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully removed custom weapon damage from item.").color(NamedTextColor.GREEN)));

        return 0;
    }

    public int get(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(
                    Component.text("You must be a Player!").color(NamedTextColor.WHITE)
            ));
            return 0;
        }

        if (!player.hasPermission("masworld.admin")) {
            player.sendMessage(TextSymbols.ERROR.append(
                    Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)
            ));
            return 0;
        }

        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
        if (meta == null || meta.getAttributeModifiers() == null) {
            player.sendMessage(TextSymbols.ERROR.append(
                    Component.text("No attributes found on this item.").color(NamedTextColor.RED)
            ));
            return 0;
        }

        Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.ATTACK_SPEED);
        if (modifiers == null || modifiers.isEmpty()) {
            player.sendMessage(TextSymbols.ERROR.append(
                    Component.text("This item has no custom attack damage.").color(NamedTextColor.RED)
            ));
            return 0;
        }

        NamespacedKey namespace = new NamespacedKey(this.main, "weapon_cooldown");

        player.getInventory().getItemInMainHand().editMeta(meta2 -> {
            modifiers.stream()
                    .filter(mod -> mod.getName().equals(namespace.getKey()))
                    .forEach(mod -> meta2.removeAttributeModifier(Attribute.ATTACK_SPEED, mod));
        });

        return 0;
    }

}
