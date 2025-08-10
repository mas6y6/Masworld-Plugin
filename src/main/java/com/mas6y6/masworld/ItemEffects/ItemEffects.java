package com.mas6y6.masworld.ItemEffects;

import com.mas6y6.masworld.ItemEffects.Objects.EffectObject;
import com.mas6y6.masworld.ItemEffects.Objects.EffectRegister;
import com.mas6y6.masworld.ItemEffects.Objects.FunctionCommands;
import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ItemEffects {
    public Masworld main;
    public Map<String, EffectRegister> effects = new HashMap<>();
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("itemeffects");

    @SuppressWarnings("unused")
    public PlayerHandler playerhandler = new PlayerHandler();
    // TODO Make this actually useful

    public File dir;
    public FunctionCommands functioncommands;

    public ItemEffects(Masworld main, File directory) {
        this.main = main;
        this.dir = directory;
        this.functioncommands = new FunctionCommands(this);
    }

    public void loadEffects() {
        effects = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        File[] files = dir.listFiles();
        if (files == null) {
            files = new File[0];  // empty array of File
        }

        for (File file : files) {
            try {
                EffectRegister effectregister = mapper.readValue(file, EffectRegister.class);
                effectregister.path = file.getPath();

                for (Map.Entry<String, EffectObject> entry : effectregister.effects.entrySet()) {
                    String key = entry.getKey();
                    EffectObject value = entry.getValue();
                    value.effectid = key;

                    NamespacedKey nsKey = Utils.parseNamespacedKey(key);

                    value.effecttype = Registry.MOB_EFFECT.getOrThrow(nsKey); // Will always supply PotionEffectType since the registry is ```Registry<PotionEffectType>```
                }

                if (effectregister.validate()) {
                    main.getLogger().info("Registered \"" + effectregister.name + "\" at \"" + effectregister.id + "\"");
                    effects.put(effectregister.id, effectregister);
                }
            } catch (Exception e) {
                main.getLogger().severe(String.format("Failed to register effect from file \"%s\": %s", file.getName(), e.getMessage()));
            }
        }
    }

    public EffectRegister getEffect(String id) {
        return this.effects.get(id);
    }

    public void registerEffect(EffectRegister effect) {
        this.effects.put(effect.id, effect);
        main.getLogger().info("Registered \""+effect.name+"\" at "+"\""+effect.id+"\"");
    }

    public void modifyEffect(String id, EffectRegister effect) {
        this.effects.replace(id, effect);
        main.getLogger().info("Modified \""+effect.name+"\"");
    }

    public List<String> getEffectIds() {
        return new ArrayList<>(effects.keySet());
    }

    public List<EffectRegister> getAllEffects() {
        return new ArrayList<>(effects.values());
    }

    public String getEffectIdAsString(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(main, "masworld_effect");

        Integer intVal = pdc.get(key, PersistentDataType.INTEGER);
        if (intVal != null) {
            return intVal.toString();
        }

        String strVal = pdc.get(key, PersistentDataType.STRING);
        if (strVal != null) {
            return strVal;
        }

        return null;
    }

    public List<EffectObject> calculateEffects(Player player) {
        PlayerInventory inventory = player.getInventory();

        Map<String, ItemStack> slotItems = Map.of(
                "helmet", inventory.getHelmet(),
                "chestplate", inventory.getChestplate(),
                "leggings", inventory.getLeggings(),
                "boots", inventory.getBoots(),
                "mainhand", inventory.getItemInMainHand(),
                "offhand", inventory.getItemInOffHand()
        );

        return slotItems.entrySet().stream()
            .filter(entry -> entry.getValue() != null && entry.getValue().getType() != Material.AIR) // skip empty/null
            .map(entry -> {
                String slotName = entry.getKey();
                String effectId = getEffectIdAsString(entry.getValue());
                if (effectId == null) return null;

                EffectRegister reg = getEffect(effectId);
                if (reg == null || reg.isDisabled()) return null;

                if (!reg.getSlots().contains(slotName.toLowerCase())) return null;
                if (reg.isOnlySneaking() && !player.isSneaking()) return null;
                if (!reg.getDimensions().stream()
                        .map(Utils::normalizeDimensionName)
                        .collect(Collectors.toSet())
                        .contains(Utils.normalizeDimensionName(player.getWorld().getName()))) return null;

                return reg.effects.values(); // Collection<EffectObject>
            })
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(
                    EffectObject::getEffectid,
                    e -> e,
                    (e1, e2) -> e1.getPriority() >= e2.getPriority() ? e1 : e2
            ))
            .values().stream()
            .toList();
    }

    public LiteralArgumentBuilder<CommandSourceStack> buildCommands() {
        // TODO Make commands

        commands.then(Commands.literal("geteffectdata").executes(functioncommands::geteffectsdata));

        return commands;
    }
}
