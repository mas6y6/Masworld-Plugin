package com.mas6y6.masworld.Registery;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Registery.Modules.EffectData;
import com.mas6y6.masworld.Registery.Modules.EffectPlayer;
import com.mas6y6.masworld.Registery.Modules.EffectRegistery;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Effects {
    private final Masworld main;
    private final Map<Integer,EffectRegistery> effects;
    private final Map<UUID, EffectPlayer> effectplayer;

    public Effects(Masworld main) {
        this.main = main;
        this.effects = new HashMap<>();
        this.effectplayer = new HashMap<>();
    }

    public void register_all_effects(File dir) {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));

        // If files is null (directory doesn't exist or isn't readable), just return
        if (files == null) {
            main.getLogger().warning("No YAML files found in: " + dir.getAbsolutePath());
            return;
        }

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            String name = config.getString("effect.name", "Unknown");
            int id = config.getInt("effect.id", -1);
            boolean disabled = config.getBoolean("effect.disabled", false);

            if (disabled) {
                main.getLogger().info("Ignoring disabled effect \"" + name + "\"");
                continue;
            }

            String itemString = config.getString("effect.item", "minecraft:air");
            Material mat = Material.matchMaterial(itemString);
            ItemStack item = (mat != null) ? new ItemStack(mat) : new ItemStack(Material.AIR);

            boolean onlySneaking = config.getBoolean("effect.conditions.only_when_sneaking", false);
            List<String> onlyInDimensions = config.getStringList("effect.conditions.only_in_dimensions");

            ConfigurationSection effectsSection = config.getConfigurationSection("effect.effects");
            Map<String, EffectData> effectDataMap = new HashMap<>();
            if (effectsSection != null) {
                for (String key : effectsSection.getKeys(false)) {
                    int amplifier = effectsSection.getInt(key + ".amplifier", 0);
                    boolean hideParticles = effectsSection.getBoolean(key + ".hideparticles", false);
                    int priority = effectsSection.getInt(key + ".priority", 0);
                    effectDataMap.put(key.toUpperCase(), new EffectData(amplifier, hideParticles, priority));
                }
            }

            List<String> slots = config.getStringList("effect.slots");

            EffectRegistery registery = new EffectRegistery(
                    name,
                    id,
                    item,
                    onlySneaking,
                    new ArrayList<>(onlyInDimensions),
                    effectDataMap,
                    new ArrayList<>(slots)
            );

            this.effects.put(id, registery);
            main.getLogger().info("Registered effect: " + name);
        }
    }

    public void register_player(UUID uuid) {
        EffectPlayer player = new EffectPlayer();
        this.effectplayer.put(uuid,player);
    }
}
