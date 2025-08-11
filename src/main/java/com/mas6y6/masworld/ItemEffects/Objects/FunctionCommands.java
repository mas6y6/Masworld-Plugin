package com.mas6y6.masworld.ItemEffects.Objects;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<EffectObject> applylist = itemeffects.calculateEffects(player);

        NamespacedKey itemeffectskey = new NamespacedKey(this.itemeffects.main, "masworld_itemapplied_effects");

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();

        String json = pdc.get(itemeffectskey, PersistentDataType.STRING);
        Map<String, Integer> oldEffectsMap = json == null ? new HashMap<>() : gson.fromJson(json, type);

        Map<String, Integer> newEffectsMap = new HashMap<>();
        for (EffectObject effect : applylist) {
            newEffectsMap.put(effect.getEffectid(), effect.getAmplifier());
        }

        for (String oldEffectId : oldEffectsMap.keySet()) {
            if (!newEffectsMap.containsKey(oldEffectId)) {
                PotionEffectType typeToRemove = PotionEffectType.getByName(oldEffectId.split(":")[1].toUpperCase());
                if (typeToRemove != null) {
                    player.removePotionEffect(typeToRemove);
                }
            }
        }

        for (EffectObject effect : applylist) {
            PotionEffectType typeToApply = PotionEffectType.getByName(effect.getEffectid().split(":")[1].toUpperCase());
            if (typeToApply != null) {
                int amplifier = effect.getAmplifier();
                int duration = Integer.MAX_VALUE; // or any large number for effectively infinite
                PotionEffect potionEffect = new PotionEffect(typeToApply, duration, amplifier, false, false, false);
                player.addPotionEffect(potionEffect, true); // true to force apply (replace weaker)
            }
        }

        pdc.set(itemeffectskey, PersistentDataType.STRING, gson.toJson(newEffectsMap));

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Applied Effects!")));

        for (EffectObject effect : applylist) {
            source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- ").append(Component.text(effect.getEffectid() + "|" + effect.getPriority()))));
        }

        return Command.SINGLE_SUCCESS;
    }
}
