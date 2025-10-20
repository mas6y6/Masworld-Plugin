package com.mas6y6.masworld.Weapons.Systems;

import com.mas6y6.masworld.Weapons.Weapons;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PumpkinBlindness {
    private BukkitTask task;

    private Weapons main;

    private static final double MAX_DISTANCE = 16.0;
    private static final double FOV_DEGREES  = 20.0;
    private static final int    CHECK_PERIOD_TICKS = 5;
    private static final int    EFFECT_DURATION_TICKS = 25;
    private static final int    EFFECT_AMPLIFIER = 0;
    private static final int    SOUND_COOLDOWN_TICKS = 40;
    private static final int    PARTICLE_COUNT = 12;

    private final Map<UUID, Integer> soundCooldown = new HashMap<>();

    public PumpkinBlindness(Weapons main) {
        this.main = main;
        Bukkit.getScheduler().runTaskTimer(this.main.main, this::tick, CHECK_PERIOD_TICKS, CHECK_PERIOD_TICKS);
    }

    private void tick() {
        soundCooldown.replaceAll((uuid, ticks) -> Math.max(0, ticks - CHECK_PERIOD_TICKS));

        List<Player> wearers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isWearingCarvedPumpkin(p)) wearers.add(p);
        }
        if (wearers.isEmpty()) return;

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (wearers.contains(viewer)) continue;
            boolean shouldBlind = false;

            for (Player wearer : wearers) {
                if (viewer.getWorld() != wearer.getWorld()) continue;

                double distSq = viewer.getEyeLocation().distanceSquared(wearer.getEyeLocation());
                if (distSq > MAX_DISTANCE * MAX_DISTANCE) continue;

                if (!viewer.hasLineOfSight(wearer)) continue;

                @NotNull org.bukkit.util.Vector toWearer = wearer.getEyeLocation().toVector()
                        .subtract(viewer.getEyeLocation().toVector())
                        .normalize();
                @NotNull Vector viewDir = viewer.getEyeLocation().getDirection().normalize();
                double dot = viewDir.dot(toWearer);

                if (dot >= Math.cos(Math.toRadians(FOV_DEGREES))) {
                    shouldBlind = true;
                    break;
                }
            }

            if (shouldBlind) {
                PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, EFFECT_DURATION_TICKS, EFFECT_AMPLIFIER, true, false, false);
                viewer.addPotionEffect(effect);

                Location eyes = viewer.getEyeLocation();
                viewer.getWorld().spawnParticle(Particle.PORTAL, eyes, PARTICLE_COUNT, 0.1, 0.1, 0.1, 0.05);

                int remaining = soundCooldown.getOrDefault(viewer.getUniqueId(), 0);
                if (remaining <= 0) {
                    viewer.playSound(eyes, Sound.ENTITY_ENDERMAN_AMBIENT, 0.4f, 0.6f);
                    soundCooldown.put(viewer.getUniqueId(), SOUND_COOLDOWN_TICKS);
                }
            }
        }
    }

    private boolean isWearingCarvedPumpkin(Player p) {
        ItemStack helmet = p.getInventory().getHelmet();

        assert helmet != null;
        PersistentDataContainerView pdc = helmet.getPersistentDataContainer();

        NamespacedKey special_effect = new NamespacedKey(this.main.main, "special_effect");

        if (pdc.has(special_effect, PersistentDataType.STRING)) {
            return Objects.equals(pdc.get(special_effect, PersistentDataType.STRING), "blindness_pumpkin");
        } else {
            return false;
        }
    }
}