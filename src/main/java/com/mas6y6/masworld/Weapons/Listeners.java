package com.mas6y6.masworld.Weapons;

import com.mas6y6.masworld.Objects.Utils;
import io.papermc.paper.registry.RegistryAccess;
import net.kyori.adventure.audience.Audience;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Listeners implements Listener {
    public Weapons weapons;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 200;

    private final Set<UUID> recentlyDropped = new HashSet<>();
    private final int dropCooldownTicks = 20;


    public Listeners(Weapons weapons) {
        this.weapons = weapons;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        Item dropped = event.getItemDrop();
        recentlyDropped.add(dropped.getUniqueId());

        Bukkit.getScheduler().runTaskLater(this.weapons.main, () -> {
            recentlyDropped.remove(dropped.getUniqueId());
        }, dropCooldownTicks);

        Bukkit.getScheduler().runTaskTimer(this.weapons.main,() -> {
            for (Player player : this.weapons.main.getServer().getOnlinePlayers()) {
                ItemMagnet(player);
            }
        }, 0L,20L);
    }


    @EventHandler
    public void onBlockBreakXP(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        NamespacedKey multiXpKey = new NamespacedKey("masworld", "multixp");
        Enchantment multiXp = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(multiXpKey);

        if (multiXp == null) return;

        if (item.containsEnchantment(multiXp)) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                if (Utils.isOre(event.getBlock())) {
                    int level = item.getEnchantmentLevel(multiXp);
                    if (level <= 0) {
                        level = 1;
                    }

                    int xp = 2 * level + 1;

                    event.getBlock().getWorld().spawn(
                            event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                            ExperienceOrb.class,
                            orb -> orb.setExperience(xp)
                    );
                }
            }
        }
    }



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        NamespacedKey multiMineKey = new NamespacedKey("masworld", "multimine");
        NamespacedKey multiXpKey = new NamespacedKey("masworld", "multixp");

        Enchantment multiMine = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(multiMineKey);
        Enchantment multiXp = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(multiXpKey);
        if (multiMine == null) return;
        if (multiXp == null) return;
        if (!item.containsEnchantment(multiMine)) return;

        int level = item.getEnchantmentLevel(multiMine);
        int radius = level;
        int forwardOffset = level;
        int upOffset = level - 1;

        int xpmultiplier = Math.max(1, item.getEnchantmentLevel(multiXp)) + 1;

        Block startBlock = event.getBlock();
        World world = startBlock.getWorld();

        int centerX = startBlock.getX() + player.getFacing().getModX() * forwardOffset;
        int centerY = startBlock.getY() + upOffset;
        int centerZ = startBlock.getZ() + player.getFacing().getModZ() * forwardOffset;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Block target = world.getBlockAt(centerX + dx, centerY + dy, centerZ + dz);

                    if (target.isEmpty() || target.getType().getHardness() < 0) continue;

                    if (item.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable meta) {
                        if (meta.getDamage() >= item.getType().getMaxDurability()) {
                            item.setAmount(0);
                            return;
                        }
                        meta.setDamage(meta.getDamage() + 1);
                        item.setItemMeta(meta);

                        target.breakNaturally(item);


                        world.spawnParticle(Particle.BLOCK_CRUMBLE, target.getLocation().add(0.5, 0.5, 0.5),
                                10, 0.3, 0.3, 0.3, target.getBlockData());

                        Color color = Color.fromRGB(255, 255, 255);

                        float scale = 0.8f;
                        Particle.DustOptions dustOptions = new Particle.DustOptions(color, scale);

                        player.getWorld().spawnParticle(Particle.DUST, target.getLocation(), 5, 0, 0, 0, 0, dustOptions);


                        if (player.getGameMode() != GameMode.CREATIVE) {
                            if (Utils.isXPOre(event.getBlock())) {
                                int xp = Utils.getOreXP(target);
                                world.spawn(target.getLocation().add(0.5, 0.5, 0.5),
                                        org.bukkit.entity.ExperienceOrb.class, orb -> orb.setExperience(xp));
                            }

                            if (item.containsEnchantment(multiXp)) {
                                world.spawn(target.getLocation().add(0.5, 0.5, 0.5),
                                        org.bukkit.entity.ExperienceOrb.class, orb -> orb.setExperience(2 * xpmultiplier));
                            }
                        }

                        if (meta.getDamage() >= item.getType().getMaxDurability()) {
                            world.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                            player.getInventory().setItemInMainHand(null);
                            item.setAmount(0);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ItemMagnet(event.getPlayer());
    }

    public void ItemMagnet(Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL) return;

        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < COOLDOWN_MS) {
            return;
        }
        cooldowns.put(player.getUniqueId(), now);

        ItemStack boots = player.getInventory().getBoots();
        if (boots == null) {
            return;
        }

        if (boots.isEmpty()) {
            return;
        }

        NamespacedKey key = new NamespacedKey("masworld", "itemmagnet");
        Enchantment itemMagnet = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(key);

        if (itemMagnet == null) {
            return;
        }

        if (!boots.containsEnchantment(itemMagnet)) {
            return;
        }

        int level = boots.getEnchantmentLevel(itemMagnet);
        double radius = 2.0 + (level * 2.0);

        int pulled = 0;

        spawnMagnetParticles(player, radius);

        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof Item dropped) {
                if (recentlyDropped.contains(dropped.getUniqueId())) continue; // skip
                dropped.teleport(player.getLocation().add(0, 0.5, 0));
            }
        }
    }

    private void spawnMagnetParticles(Player player, double radius) {
        Location center = player.getLocation().add(0, 1, 0);
        int points = Math.max(10, (int) (10 * radius));

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location particleLoc = center.clone().add(x, 0, z);
            Color color = Color.fromRGB(
                    (int) (0.69 * 255),
                    (int) (0.44 * 255),
                    (int) (1.0 * 255)
            );

            float scale = 0.8f;
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, scale);

            player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, dustOptions);
        }
    }

    @EventHandler
    public void dynamiteThrow(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();

        if (projectile.getShooter() instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();

                NamespacedKey specialEffectId = new NamespacedKey(this.weapons.main, "special_effect");
                NamespacedKey powerkey = new NamespacedKey(this.weapons.main, "dynamite_power");
                NamespacedKey fusekey = new NamespacedKey(this.weapons.main, "dynamite_fuse");

                if (container.has(specialEffectId, PersistentDataType.STRING)) {
                    String effect = container.get(specialEffectId, PersistentDataType.STRING);
                    Long fuse = Objects.requireNonNull(container.get(fusekey, PersistentDataType.LONG));
                    Float power = Objects.requireNonNull(container.get(powerkey, PersistentDataType.FLOAT));

                    if (effect != null) {
                        projectile.getPersistentDataContainer()
                                .set(specialEffectId, PersistentDataType.STRING, effect);

                        projectile.getPersistentDataContainer()
                                .set(powerkey, PersistentDataType.FLOAT, power);

                        projectile.getPersistentDataContainer()
                                .set(fusekey, PersistentDataType.LONG, fuse);
                    }
                }
            }
        }
    }

    @EventHandler
    public void dynamiteHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;

        Projectile projectile = event.getEntity();
        NamespacedKey specialEffectId = new NamespacedKey(this.weapons.main, "special_effect");
        NamespacedKey powerkey = new NamespacedKey(this.weapons.main, "dynamite_power");
        NamespacedKey fusekey = new NamespacedKey(this.weapons.main, "dynamite_fuse");

        if (projectile.getPersistentDataContainer().has(specialEffectId, PersistentDataType.STRING)) {
            String effect = projectile.getPersistentDataContainer().get(specialEffectId, PersistentDataType.STRING);
            Long fuse = Objects.requireNonNull(projectile.getPersistentDataContainer().get(fusekey, PersistentDataType.LONG));
            Float power = Objects.requireNonNull(projectile.getPersistentDataContainer().get(powerkey, PersistentDataType.FLOAT));

            if ("dynamite".equals(effect)) {
                snowball.getWorld().playSound(snowball.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0F, 1.0F);

                ItemStack dynamiteItem = new ItemStack(Material.SNOWBALL);
                ItemMeta meta = dynamiteItem.getItemMeta();
                meta.setItemModel(new NamespacedKey("masworld","dynamite"));
                dynamiteItem.setItemMeta(meta);

                ItemDisplay itemDisplay =  snowball.getWorld().spawn(snowball.getLocation(),ItemDisplay.class,d -> {
                    d.setItemStack(dynamiteItem);
                    d.setGravity(false);
                    d.setInterpolationDuration(0);
                    d.setBillboard(Display.Billboard.FIXED);
                });

                final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.weapons.main, () -> {
                    Location currentLoc = snowball.getLocation();
                    currentLoc.getWorld().spawnParticle(
                            Particle.SMOKE,
                            currentLoc,
                            8,
                            0.2, 0.2, 0.2,
                            0.01           // speed
                    );

                    currentLoc.getWorld().spawnParticle(
                            Particle.FLAME,
                            currentLoc,
                            8,
                            0.2, 0.2, 0.2,
                            0.01           // speed
                    );
                }, 0L, 5L);

                Bukkit.getScheduler().runTaskLater(this.weapons.main, () -> {
                    if (!snowball.isDead()) {
                        snowball.remove();
                    }

                    Bukkit.getScheduler().cancelTask(taskId);
                    itemDisplay.remove();

                    snowball.getWorld().createExplosion(snowball.getLocation(), power, true, true);
                }, fuse);
            }
        }
    }
}