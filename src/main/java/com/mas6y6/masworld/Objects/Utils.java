package com.mas6y6.masworld.Objects;

import com.mas6y6.masworld.Objects.Exceptions.IllegalKeyException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Utils {
    public static NamespacedKey parseNamespacedKey(String key) {
        if (!key.contains(":")) {
            throw new IllegalKeyException("Key must contain a namespace (e.g. 'minecraft:speed')");
        }

        String[] parts = key.split(":", 2);
        String namespace = parts[0];
        String path = parts[1];

        if (namespace.isEmpty() || path.isEmpty()) {
            throw new IllegalKeyException("Namespace or key cannot be empty");
        }

        if (!namespace.matches("[a-z0-9_.-]+")) {
            throw new IllegalKeyException("Illegal namespace format: " + namespace);
        }

        return new NamespacedKey(namespace, path);
    }

    public static String normalizeSlotName(String slot) {
        return slot == null ? "" : slot.toLowerCase(Locale.ROOT);
    }

    public static String normalizeDimensionName(String dim) {
        if (dim == null) return "";
        dim = dim.toLowerCase();

        if (dim.contains("nether")) return "nether";
        if (dim.contains("end") || dim.contains("the_end")) return "end";
        if (dim.contains("world")) return "overworld";

        return dim;
    }

    public static List<String> getAllPotionsKeys() {
        List<PotionEffectType> effects = Registry.EFFECT.keyStream()
                .map(Registry.EFFECT::get)
                .filter(Objects::nonNull)
                .toList();

        return effects.stream()
                .map(effect -> effect.getKey().toString())
                .toList();
    }

    public static List<String> getDimensions() {
        return Bukkit.getWorlds().stream().map(World::getName).toList();
    }
}
