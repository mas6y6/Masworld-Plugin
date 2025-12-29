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

public class EvokerBookSpacing{
    public Masworld main;
    public String attributetarget = "evoker_book_spacing";

    public EvokerBookSpacing(Masworld plugin) {
        this.main = plugin;
    }

    public int set(CommandContext<CommandSourceStack> context) {
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

        NamespacedKey namespace = new NamespacedKey(this.main, attributetarget);

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            meta.getPersistentDataContainer().set(namespace, PersistentDataType.DOUBLE, value);
        });

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully change \"masworld:"+attributetarget+"\" = \""+ value +"\".").color(NamedTextColor.GREEN)));

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

        NamespacedKey namespace = new NamespacedKey(this.main, "shulker_sword_range");

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            meta.getPersistentDataContainer().remove(namespace);
        });

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully removed \"masworld:"+attributetarget+"\" from item.").color(NamedTextColor.GREEN)));

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

        NamespacedKey namespace = new NamespacedKey(this.main, "shulker_sword_range");

        Double value = player.getInventory().getItemInMainHand().getPersistentDataContainer().get(namespace,PersistentDataType.DOUBLE);

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("\"masworld:"+attributetarget+"\" = \""+ value +"\"").color(NamedTextColor.GREEN)));

        return 0;
    }
}