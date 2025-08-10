package com.mas6y6.masworld.ItemEffects.Objects;

import com.mas6y6.masworld.ItemEffects.ItemEffects;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class FunctionCommands {
    public ItemEffects itemeffects;

    public FunctionCommands(ItemEffects itemEffects) {
        this.itemeffects = itemEffects;
    }

    public int geteffectsdata(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!")));
            return 0;
        } else {
            List<EffectObject> list = itemeffects.calculateEffects(player);

            if (!(list.size() == 0)) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Effects To Apply:")));
            }

            for (EffectObject effect : list) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- ").append(Component.text(effect.getEffectid() + "|" + effect.getPriority()))));
            }

            if (list.size() == 0) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("No effects")));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    public int applyEffects(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!")));
            return 0;
        }

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(this.itemeffects.main, "masworld_effect");

        return Command.SINGLE_SUCCESS;
    }
}
