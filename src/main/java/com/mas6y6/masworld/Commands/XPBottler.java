package com.mas6y6.masworld.Commands;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mas6y6.masworld.Objects.Utils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

public class XPBottler {
    public Masworld masworld;
    public LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("xpbottler")
            .executes(this::XPBottlerCommand) // default, no args = 1 bottle
            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                    .executes(this::XPBottlerCommandWithAmount));

    public XPBottler(Masworld main) {
        this.masworld = main;
    }

    private int XPBottlerCommand(CommandContext<CommandSourceStack> context) {
        return runCommand(context, 1);
    }

    private int XPBottlerCommandWithAmount(CommandContext<CommandSourceStack> context) {
        int amount = IntegerArgumentType.getInteger(context, "amount");
        return runCommand(context, amount);
    }

    private int runCommand(CommandContext<CommandSourceStack> context, int amount) {
        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(
                    Component.text("You must be a Player!").color(NamedTextColor.RED)));
            return 0;
        }

        int totalXP = Utils.getTotalExp(player);
        int requiredXP = 15 * amount;

        if (totalXP < requiredXP) {
            player.sendMessage(TextSymbols.ERROR.append(
                    Component.text("You don’t have enough XP! (Need " + requiredXP + " points)")
                            .color(NamedTextColor.RED)));
            return 0;
        }

        int bottlesFound = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.getType().getKey().equals(NamespacedKey.minecraft("glass_bottle"))) {
                bottlesFound += invItem.getAmount();
            }
        }

        if (bottlesFound < amount) {
            player.sendMessage(TextSymbols.ERROR.append(
                    Component.text("You don’t have enough Glass Bottles! (Need " + amount + ")")
                            .color(NamedTextColor.RED)));
            return 0;
        }

        int toRemove = amount;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (toRemove <= 0) break;
            if (invItem != null && invItem.getType().getKey().equals(NamespacedKey.minecraft("glass_bottle"))) {
                int stackAmount = invItem.getAmount();
                if (stackAmount > toRemove) {
                    invItem.setAmount(stackAmount - toRemove);
                    toRemove = 0;
                } else {
                    player.getInventory().removeItem(invItem);
                    toRemove -= stackAmount;
                }
            }
        }

        Utils.takeExpPoints(player, requiredXP);

        ItemStack xpBottle = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ITEM)
                .getOrThrow(NamespacedKey.minecraft("experience_bottle"))
                .createItemStack(amount);

        player.getInventory().addItem(xpBottle);

        player.sendMessage(TextSymbols.SUCCESS.append(
                Component.text("Bottled " + amount + " XP Bottle!")
                        .color(NamedTextColor.GREEN)));

        return 1;
    }
}

