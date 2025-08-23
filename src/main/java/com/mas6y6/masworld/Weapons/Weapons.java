package com.mas6y6.masworld.Weapons;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Weapons {
    public Masworld main;
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("specialeffects");

    public Weapons(Masworld main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(new Listeners(this), this.main);
    }

    public LiteralArgumentBuilder<CommandSourceStack> buildCommands() {
        commands.then(Commands.literal("geteffectdata").then(Commands.argument("id", StringArgumentType.word()).executes(this::registerData)));

        return commands;
    }

    public int registerData(CommandContext<CommandSourceStack> context) {
        String id = context.getArgument("id",String.class);

        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        NamespacedKey specialEffectId = new NamespacedKey(this.main, "special_effect");

        player.getInventory().getItemInMainHand().editMeta(meta -> {
            meta.getPersistentDataContainer().set(specialEffectId, PersistentDataType.STRING, "I love Tacos!");
        });

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully registered \""+id+"\" to item.").color(NamedTextColor.GREEN)));

        return 0;
    }
}
