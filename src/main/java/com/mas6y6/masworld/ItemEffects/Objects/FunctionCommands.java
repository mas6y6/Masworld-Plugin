package com.mas6y6.masworld.ItemEffects.Objects;

import com.mas6y6.masworld.ItemEffects.ItemEffects;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;

public class FunctionCommands {
    public ItemEffects itemeffects;

    public FunctionCommands(ItemEffects itemEffects) {
        this.itemeffects = itemEffects;
    }

    public int getEffectsDataCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!")));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.debug"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.WHITE)));
                return 0;
            }

            List<EffectObject> list = itemeffects.calculateEffects(player);

            if (!(list.size() == 0)) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Effects To Apply:").color(NamedTextColor.WHITE)));
            }

            for (EffectObject effect : list) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- ").append(Component.text(effect.getEffectid() + " | " + effect.getPriority()).color(NamedTextColor.WHITE))));
            }

            if (list.size() == 0) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("No effects")));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    public int applyEffectsCommannd(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.debug"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        List<EffectObject> applylist = itemeffects.applyEffects(player);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Applied Effects!").color(NamedTextColor.GREEN)));

        for (EffectObject effect : applylist) {
            source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- ").append(Component.text(effect.getEffectid() + " | " + effect.getPriority()).color(NamedTextColor.WHITE))));
        }

        return Command.SINGLE_SUCCESS;
    }
}
