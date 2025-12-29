package com.mas6y6.masworld.Items.Attributes;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class SpecialEffect {
    public Masworld main;

    public SpecialEffect(Masworld plugin) {
        this.main = plugin;
    }

    public int set(CommandContext<CommandSourceStack> context) {
        String value = context.getArgument("value",String.class);

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

        NamespacedKey specialEffectId = new NamespacedKey(this.main, "special_effect");

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            meta.getPersistentDataContainer().set(specialEffectId, PersistentDataType.STRING, value);
        });

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully change \"masworld:special_effect\" = \""+ value +"\".").color(NamedTextColor.GREEN)));

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

        NamespacedKey specialEffectId = new NamespacedKey(this.main, "special_effect");

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            meta.getPersistentDataContainer().remove(specialEffectId);
        });

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully removed \"masworld:special_effect\" from item.").color(NamedTextColor.GREEN)));

        return 0;
    }

    public int get(CommandContext<CommandSourceStack> context) {
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

        NamespacedKey specialEffectId = new NamespacedKey(this.main, "special_effect");

        String id = player.getInventory().getItemInMainHand().getPersistentDataContainer().get(specialEffectId,PersistentDataType.STRING);

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("\"masworld:special_effect\" = \""+id+"\"").color(NamedTextColor.GREEN)));

        return 0;
    }
}
