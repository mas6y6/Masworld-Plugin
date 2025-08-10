package com.mas6y6.masworld.ItemEffects;

import com.mas6y6.masworld.ItemEffects.Objects.EffectObject;
import com.mas6y6.masworld.ItemEffects.Objects.EffectRegister;
import com.mas6y6.masworld.Objects.Exceptions.IllegalKeyException;
import com.mas6y6.masworld.Masworld;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ItemEffects {
    private final Masworld main;
    public Map<String, EffectRegister> effects = new HashMap<>();
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("itemeffects");
    public PlayerHandler playerhandler = new PlayerHandler();
    public File dir;

    public ItemEffects(Masworld main, File directory) {
        this.main = main;
        this.dir = directory;
    }

    public void loadEffects() throws IOException {
        effects = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for (File file : dir.listFiles()) {
            try {
                EffectRegister effectregister = mapper.readValue(file, EffectRegister.class);
                effectregister.path = file.getPath();
                main.getLogger().info("Registered \""+effectregister.name+"\" at "+"\""+effectregister.id+"\"");

                for (Map.Entry<String, EffectObject> entry : effectregister.effects.entrySet()) {
                    String key = entry.getKey();

                    if (!key.contains(":")) {
                        throw new IllegalKeyException("Key must contain a namespace");
                    } else {
                        String[] parts = key.split(":", 2);
                        String namespace = parts[0];
                        String path = parts[1];

                        if (namespace.isEmpty() || path.isEmpty()) {
                            throw new IllegalKeyException("Key must contain a namespace");
                        }
                        if (!namespace.matches("[a-z0-9_.-]+")) {
                            throw new IllegalKeyException("Illegal Key");
                        }
                    }

                    if (key.contains(":")) {

                    }
                }


                effects.put(effectregister.id,effectregister);
            } catch(Exception e) {
                this.main.getLogger().severe("Error Registering \"" + file.getName() + "\": " + e.getMessage());
                e.printStackTrace();
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


    public void calculateEffects(Player player) {
        PlayerInventory inventory = player.getInventory();
        NamespacedKey playerKey = new NamespacedKey(this.main, "masworld_applied_effects");

        PersistentDataContainer container = player.getPersistentDataContainer();
        List<String> oldEffects = container.get(playerKey, PersistentDataType.LIST.strings());

        String[] itemEffectIds = {
                getEffectIdAsString(inventory.getHelmet()),
                getEffectIdAsString(inventory.getChestplate()),
                getEffectIdAsString(inventory.getLeggings()),
                getEffectIdAsString(inventory.getBoots()),
                getEffectIdAsString(inventory.getItemInMainHand()),
                getEffectIdAsString(inventory.getItemInOffHand())
        };

        Set<String> uniqueEffectIds = Arrays.stream(itemEffectIds)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<EffectRegister> highestPriorityEffects = uniqueEffectIds.stream()
                .map(this::getEffect)
                .filter(Objects::nonNull)
                .map(effectReg -> {
                    EffectRegister copy = effectReg.copy();
                    Map.Entry<String, EffectObject> highPriorityEntry = copy.effects.entrySet().stream()
                            .max(Comparator.comparingInt(e -> e.getValue().priority))
                            .orElse(null);

                    if (highPriorityEntry != null) {
                        copy.effects.clear();
                        copy.effects.put(highPriorityEntry.getKey(), highPriorityEntry.getValue());
                        return copy;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        // At this point, highestPriorityEffects contains only the best effects per item
        // TODO: Apply these effects to the player, replacing oldEffects if needed

        if (oldEffects != null) {

        }
    }
}
