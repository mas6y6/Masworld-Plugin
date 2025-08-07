package com.mas6y6.masworld.Commands;

import com.mas6y6.masworld.Masworld;
import com.mojang.brigadier.context.CommandContext;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Function;

public class FixItems {
    private final Masworld main;

    public FixItems(Masworld main) {
        this.main = main;
    }

    public int effect(CommandContext context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if ( !source.getSender().hasPermission("masworld.fixitems") ) {
            source.getSender().sendMessage(Component.text("You do not have permission to run this command").color(NamedTextColor.RED));
            return 1;
        }

        CommandSender sender = source.getSender();
        if (sender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.isEmpty()) {
                source.getSender().sendMessage(Component.text("Must be holding a item.").color(NamedTextColor.RED));
                return 1;
            }
            int newnbt = NBT.get(item, (Function<ReadableItemNBT, Byte>) nbt -> nbt.getOrDefault("masworld_effect", (byte) 0));
            NBT.modify(item, nbt -> {
                nbt.removeKey("PublicBukkitValues");
            });

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                sender.sendMessage(Component.text("This item has no metadata.").color(NamedTextColor.RED));
                return 1;
            }

            NBT.modify(item, nbt -> {
                nbt.removeKey("PublicBukkitValues.wolfyutilities:custom_item");
                nbt.removeKey("masworld_effect");
            });

            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(main, "masworld_effect");

            container.set(key, PersistentDataType.INTEGER, newnbt);

            item.setItemMeta(meta);

            source.getSender().sendMessage(Component.text("Item Fixed! It should work now!").color(NamedTextColor.GREEN));
        } else {
            source.getSender().sendMessage(Component.text("Must be a player to run this command!").color(NamedTextColor.RED));
            return 0;
        }

        return 1;
    }

    public int upgrade_template(CommandContext context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if ( !source.getSender().hasPermission("masworld.fixitems") ) {
            source.getSender().sendMessage(Component.text("You do not have permission to run this command").color(NamedTextColor.RED));
            return 1;
        }

        CommandSender sender = source.getSender();
        if (sender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.isEmpty()) {
                source.getSender().sendMessage(Component.text("Must be holding a item.").color(NamedTextColor.RED));
                return 1;
            }
            int newnbt = NBT.get(item, (Function<ReadableItemNBT, Integer>) nbt -> nbt.getOrDefault("masworld_upgrade_to", 0));

            NBT.modify(item, nbt -> {
                nbt.removeKey("PublicBukkitValues");
            });

            NBT.modify(item, nbt -> {
                nbt.removeKey("masworld_upgrade_to");
            });

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                sender.sendMessage(Component.text("This item has no metadata.").color(NamedTextColor.RED));
                return 1;
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(main, "masworld_upgradetemplate");

            container.set(key, PersistentDataType.INTEGER, newnbt);

            item.setItemMeta(meta);

            source.getSender().sendMessage(Component.text("Item Fixed! It should work now!").color(NamedTextColor.GREEN));
        } else {
            source.getSender().sendMessage(Component.text("Must be a player to run this command!").color(NamedTextColor.RED));
            return 0;
        }

        return 1;
    }

    public int upgrade_data(CommandContext context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if ( !source.getSender().hasPermission("masworld.fixitems") ) {
            source.getSender().sendMessage(Component.text("You do not have permission to run this command").color(NamedTextColor.RED));
            return 1;
        }

        CommandSender sender = source.getSender();
        if (sender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.isEmpty()) {
                source.getSender().sendMessage(Component.text("Must be holding a item.").color(NamedTextColor.RED));
                return 1;
            }
            String newnbt1 = NBT.get(item, (Function<ReadableItemNBT, String>) nbt -> nbt.getOrDefault("masworld_upgrade_tier", ""));
            String newnbt2 = NBT.get(item, (Function<ReadableItemNBT, String>) nbt -> nbt.getOrDefault("masworld_upgrade_item", ""));

            NBT.modify(item, nbt -> {
                nbt.removeKey("PublicBukkitValues");
            });

            NBT.modify(item, nbt -> {
                nbt.removeKey("masworld_upgrade_tier");
                nbt.removeKey("masworld_upgrade_item");
            });

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                sender.sendMessage(Component.text("This item has no metadata.").color(NamedTextColor.RED));
                return 1;
            }

            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey tier = new NamespacedKey(main, "masworld_upgrade_tier");
            NamespacedKey masworlditem = new NamespacedKey(main, "masworld_item");

            container.set(tier, PersistentDataType.STRING, newnbt1);
            container.set(masworlditem, PersistentDataType.STRING, newnbt2);

            item.setItemMeta(meta);

            source.getSender().sendMessage(Component.text("Item Fixed! It should work now!").color(NamedTextColor.GREEN));
        } else {
            source.getSender().sendMessage(Component.text("Must be a player to run this command!").color(NamedTextColor.RED));
            return 0;
        }

        return 1;
    }

    public int recipe_item(CommandContext context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if ( !source.getSender().hasPermission("masworld.fixitems") ) {
            source.getSender().sendMessage(Component.text("You do not have permission to run this command").color(NamedTextColor.RED));
            return 1;
        }

        CommandSender sender = source.getSender();
        if (sender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.isEmpty()) {
                source.getSender().sendMessage(Component.text("Must be holding a item.").color(NamedTextColor.RED));
                return 1;
            }
            String newnbt1 = NBT.get(item, (Function<ReadableItemNBT, String>) nbt -> nbt.getOrDefault("masworld_recipe_item", ""));

            NBT.modify(item, nbt -> {
                nbt.removeKey("PublicBukkitValues");
            });

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                sender.sendMessage(Component.text("This item has no metadata.").color(NamedTextColor.RED));
                return 1;
            }

            NBT.modify(item, nbt -> {
                nbt.removeKey("masworld_recipe_item");
            });

            PersistentDataContainer container = meta.getPersistentDataContainer();
            NamespacedKey tier = new NamespacedKey(main, "masworld_recipe_item");

            container.set(tier, PersistentDataType.STRING, newnbt1);

            item.setItemMeta(meta);
            source.getSender().sendMessage(Component.text("Item Fixed! It should work now!").color(NamedTextColor.GREEN));
        } else {
            source.getSender().sendMessage(Component.text("Must be a player to run this command!").color(NamedTextColor.RED));
            return 0;
        }

        return 1;
    }
}
