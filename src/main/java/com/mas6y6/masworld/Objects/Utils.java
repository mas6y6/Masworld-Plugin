package com.mas6y6.masworld.Objects;

import com.mas6y6.masworld.Objects.Exceptions.IllegalKeyException;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Utils {
    private static final Random RANDOM = new Random();

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
        List<PotionEffectType> effects = Registry.MOB_EFFECT.keyStream()
                .map(Registry.MOB_EFFECT::get)
                .filter(Objects::nonNull)
                .toList();

        return effects.stream()
                .map(effect -> effect.getKey().toString())
                .toList();
    }

    public static List<String> getDimensions() {
        return Bukkit.getWorlds().stream().map(World::getName).toList();
    }

    public static Registry<@NotNull Enchantment> getEnchantmentRegistry() {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    }

    public static int getOreXP(Block block, int multi) {
        int xp = 0;

        switch (block.getType()) {
            case COAL_ORE, DEEPSLATE_COAL_ORE -> xp = RANDOM.nextInt(3);           // 0-2
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE, EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> xp = 3 + RANDOM.nextInt(5); // 3-7
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> xp = 2 + RANDOM.nextInt(4);      // 2-5
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE, NETHER_QUARTZ_ORE -> xp = 1 + RANDOM.nextInt(5); // 1-5
            default -> xp = 0;
        }

        return xp * multi;
    }

    public static int getOreXP(Block block) {
        return getOreXP(block, 1);
    }

    public static boolean isOre(Block block) {
        Material blockType = block.getType();

        if (blockType == Material.COAL_ORE ||
                blockType == Material.IRON_ORE ||
                blockType == Material.GOLD_ORE ||
                blockType == Material.DIAMOND_ORE ||
                blockType == Material.EMERALD_ORE ||
                blockType == Material.LAPIS_ORE ||
                blockType == Material.REDSTONE_ORE ||
                blockType == Material.NETHER_QUARTZ_ORE ||
                blockType == Material.NETHER_GOLD_ORE ||
                blockType == Material.DEEPSLATE_COAL_ORE ||
                blockType == Material.DEEPSLATE_IRON_ORE ||
                blockType == Material.DEEPSLATE_GOLD_ORE ||
                blockType == Material.DEEPSLATE_DIAMOND_ORE ||
                blockType == Material.DEEPSLATE_EMERALD_ORE ||
                blockType == Material.DEEPSLATE_LAPIS_ORE ||
                blockType == Material.DEEPSLATE_REDSTONE_ORE ) {
                return true;
        }

        return false;
    }

    public static boolean isXPOre(Block block) {
        Material blockType = block.getType();

        if (blockType == Material.COAL_ORE ||
                blockType == Material.DIAMOND_ORE ||
                blockType == Material.EMERALD_ORE ||
                blockType == Material.LAPIS_ORE ||
                blockType == Material.REDSTONE_ORE ||
                blockType == Material.NETHER_QUARTZ_ORE ||
                blockType == Material.NETHER_GOLD_ORE ||
                blockType == Material.DEEPSLATE_DIAMOND_ORE ||
                blockType == Material.DEEPSLATE_EMERALD_ORE ||
                blockType == Material.DEEPSLATE_LAPIS_ORE ||
                blockType == Material.DEEPSLATE_REDSTONE_ORE ||
                blockType == Material.DEEPSLATE_COAL_ORE) {
            return true;
        }

        return false;
    }

    public static Component createEnchantmentComponent(String unicode, NamedTextColor color, String title) {
        Component icon = Component.text(unicode).color(color);
        Component text = Component.text(" " + title);
        return icon.append(text);
    }

    public static Component createEnchantmentComponent(String unicode, TextColor color, String title) {
        Component icon = Component.text(unicode).color(color);
        Component text = Component.text(" " + title);
        return icon.append(text);
    }

    private static int getExpAtLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        }
        if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        }
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    public static void takeExpPoints(Player player, int points) {
        int total = getTotalExp(player);
        int newTotal = Math.max(0, total - points);

        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        player.giveExp(newTotal);
    }
    public static int getTotalExp(Player player) {
        int level = player.getLevel();
        float progress = player.getExp();

        int expToNext;
        if (level >= 30) {
            expToNext = 9 * level - 158;
        } else if (level >= 15) {
            expToNext = 5 * level - 38;
        } else {
            expToNext = 2 * level + 7;
        }

        int expThisLevel = Math.round(progress * expToNext);
        return getExpAtLevel(level) + expThisLevel;
    }
}
