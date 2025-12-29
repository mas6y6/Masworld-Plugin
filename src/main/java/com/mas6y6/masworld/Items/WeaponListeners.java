package com.mas6y6.masworld.Items;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mas6y6.masworld.Objects.Utils;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.plugin.gui.CraftEngineGUIHolder;
import net.momirealms.craftengine.core.plugin.CraftEngine;
import net.momirealms.craftengine.libraries.nbt.CompoundTag;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class WeaponListeners implements Listener {
    public Items weapons;

    public WeaponListeners(Items weapons) {
        this.weapons = weapons;
    }

    public static long getLongPDC(PersistentDataContainer container, NamespacedKey key) {
        try {
            Long fuseLong = container.get(key, PersistentDataType.LONG);
            if (fuseLong != null) return fuseLong;
        } catch (IllegalArgumentException ignored) {}

        try {
            Integer fuseInt = container.get(key, PersistentDataType.INTEGER);
            if (fuseInt != null) return fuseInt.longValue();
        } catch (IllegalArgumentException ignored) {}
        return 0L;
    }

    public static float getFloatPDC(PersistentDataContainer container, NamespacedKey key) {
        try {
            Double valueDouble = container.get(key, PersistentDataType.DOUBLE);
            if (valueDouble != null) return valueDouble.floatValue();
        } catch (IllegalArgumentException ignored) {}

        try {
            Float valueFloat = container.get(key, PersistentDataType.FLOAT);
            if (valueFloat != null) return valueFloat;
        } catch (IllegalArgumentException ignored) {}

        return 0f;
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
                    if (Objects.equals(container.get(specialEffectId, PersistentDataType.STRING), "dynamite")) {
                        String effect = Objects.requireNonNull(container.get(specialEffectId, PersistentDataType.STRING));
                        Long fuse = getLongPDC(container,fusekey);
                        Float power = getFloatPDC(container,powerkey);

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

    @EventHandler
    public void sandStormBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack bow = event.getBow();
        if (bow == null) return;

        NamespacedKey special_effectkey = new NamespacedKey(this.weapons.main, "special_effect");

        String effect = bow.getPersistentDataContainer().get(special_effectkey, PersistentDataType.STRING);
        if (effect != null && effect.equalsIgnoreCase("sandstorm_bow")) {
            Bukkit.getScheduler().runTaskLater(this.weapons.main, () -> {
                if (event.getProjectile() instanceof Arrow originalArrow) {
                    float force = event.getForce();
                    Vector direction = player.getEyeLocation().getDirection().normalize();

                    Arrow extraArrow = player.getWorld().spawn(player.getEyeLocation(), Arrow.class);

                    extraArrow.setVelocity(direction.multiply(force * 3));
                    extraArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    extraArrow.setShooter(player);
                    extraArrow.setCritical(originalArrow.isCritical());
                }
            }, 12L);
        }
    }

    private final Map<UUID, Long> shulkercooldowns = new HashMap<>();

    @EventHandler
    public void shulkerSword(PlayerInteractEvent event) {
        NamespacedKey special_effectKey = new NamespacedKey(this.weapons.main, "special_effect");
        NamespacedKey cooldownKey = new NamespacedKey(this.weapons.main, "shulker_sword_cooldown");
        NamespacedKey bulletsKey = new NamespacedKey(this.weapons.main, "shulker_sword_bullet");
        NamespacedKey rangeKey = new NamespacedKey(this.weapons.main, "shulker_sword_range");

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(special_effectKey, PersistentDataType.STRING)) {
            String value = container.get(special_effectKey, PersistentDataType.STRING);
            if ("shulker_sword".equals(value)) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Player player = event.getPlayer();

                    // Check cooldown value from item, default to 15000ms
                    long itemCooldown = container.getOrDefault(cooldownKey, PersistentDataType.LONG, 15000L);
                    int bullets = container.getOrDefault(bulletsKey, PersistentDataType.INTEGER, 1);
                    double range = container.getOrDefault(rangeKey, PersistentDataType.DOUBLE, 15.0);

                    long now = System.currentTimeMillis();
                    long last = shulkercooldowns.getOrDefault(player.getUniqueId(), 0L);
                    if (now - last < itemCooldown) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                        player.sendActionBar(Component.text("Under cooldown!").color(NamedTextColor.RED));
                        return;
                    }

                    LivingEntity target = getNearestTarget(player, range);

                    if (target != null) {
                        shulkercooldowns.put(player.getUniqueId(), now);

                        if (target instanceof Player player1) {
                            player.sendActionBar(
                                    Component.text("Shulker sword targeting: ")
                                            .color(NamedTextColor.WHITE)
                                            .append(
                                                    Component.text(player1.getName())
                                                            .color(NamedTextColor.GREEN)
                                            )
                            );
                        }

                        player.getLocation().getWorld().playSound(
                                player.getLocation(),
                                org.bukkit.Sound.ENTITY_SHULKER_SHOOT,
                                1.0f,
                                1.0f
                        );

                        for (int i = 0; i < bullets; i++) {
                            ShulkerBullet bullet = (ShulkerBullet) player.getWorld().spawnEntity(
                                    player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)),
                                    EntityType.SHULKER_BULLET
                            );

                            bullet.setTarget(target);
                        }
                    } else {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                        player.sendActionBar(Component.text("No entity found!").color(NamedTextColor.RED));
                    }
                }
            }
        }
    }

    private LivingEntity getNearestTarget(Player source, double range) {
        World world = source.getWorld();
        Location loc = source.getLocation();
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity e : world.getNearbyEntities(loc, range, range, range)) {
            if (!(e instanceof Player)) continue;
            Player target = (Player) e;

            if (target.equals(source)) continue;
            if (target.getGameMode() != GameMode.SURVIVAL) continue;

            double dist = target.getLocation().distanceSquared(loc);
            if (dist < nearestDistance) {
                nearest = target;
                nearestDistance = dist;
            }
        }
        if (nearest == null) {
            for (Entity e : world.getNearbyEntities(loc, range, range, range)) {
                if (!(e instanceof Monster)) continue;
                LivingEntity le = (LivingEntity) e;

                double dist = le.getLocation().distanceSquared(loc);
                if (dist < nearestDistance) {
                    nearest = le;
                    nearestDistance = dist;
                }
            }
        }

        return nearest;
    }

    private final Map<UUID, Long> evokercooldowns = new HashMap<>();

    @EventHandler
    public void evokerBook(PlayerInteractEvent event) {
        NamespacedKey special_effectKey = new NamespacedKey(this.weapons.main, "special_effect"); // STR
        NamespacedKey evoker_book_range = new NamespacedKey(this.weapons.main, "evoker_book_range"); // INT
        NamespacedKey evoker_book_angle = new NamespacedKey(this.weapons.main, "evoker_book_angle"); // INT
        NamespacedKey evoker_book_beamcount = new NamespacedKey(this.weapons.main, "evoker_book_beamcount"); // INT
        NamespacedKey evoker_book_spacing = new NamespacedKey(this.weapons.main, "evoker_book_spacing"); // DOUBLE
        NamespacedKey evoker_book_cooldown = new NamespacedKey(this.weapons.main, "evoker_book_cooldown"); // LONG

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Default cooldown = 30s

        if (!container.has(special_effectKey, PersistentDataType.STRING)) return;
        if (!Objects.equals(container.get(special_effectKey, PersistentDataType.STRING), "evoker_book")) return;

        long itemCooldown = container.getOrDefault(evoker_book_cooldown, PersistentDataType.LONG, 30000L);

        long now = System.currentTimeMillis();
        UUID uuid = event.getPlayer().getUniqueId();
        long last = evokercooldowns.getOrDefault(uuid, 0L);

        if (now - last < itemCooldown) {
            long remaining = (itemCooldown - (now - last)) / 1000; // show seconds
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            event.getPlayer().sendActionBar(Component.text("Cooldown: " + remaining + "s").color(NamedTextColor.RED));
            return;
        }

        Player player = event.getPlayer();

        int beamCount = container.getOrDefault(evoker_book_beamcount, PersistentDataType.INTEGER, 10);
        double spacing = container.getOrDefault(evoker_book_spacing, PersistentDataType.DOUBLE, 1.0);
        int beamLength = container.getOrDefault(evoker_book_range, PersistentDataType.INTEGER, beamCount);
        double totalAngle = container.getOrDefault(evoker_book_angle, PersistentDataType.DOUBLE, 180.0);
        double fangGap = 3.0;

        Vector direction = player.getEyeLocation().getDirection().clone();
        direction.setY(0).normalize();

        Location baseLoc = player.getLocation().clone().add(direction.clone());

        int batchSize = 1;
        long delay = 3L;
        UUID playerId = player.getUniqueId();
        Masworld plugin = this.weapons.main;

        Runnable spawnFangsRunnable = () -> {
            new BukkitRunnable() {
                int spawned = 0;

                @Override
                public void run() {
                    if (spawned >= beamCount) {
                        this.cancel();
                        return;
                    }

                    Player p = Bukkit.getPlayer(playerId);
                    if (p == null || !p.isOnline()) {
                        this.cancel();
                        return;
                    }

                    World w = p.getWorld();

                    for (int j = 0; j < batchSize && spawned < beamCount; j++) {
                        double angle = -totalAngle / 2 + (totalAngle / (beamCount - 1)) * spawned;
                        Vector fangDir = direction.clone().rotateAroundY(Math.toRadians(angle));

                        for (int i = 1; i <= beamLength; i++) {
                            Location fangLoc = baseLoc.clone().add(fangDir.clone().multiply(fangGap + (i - 1) * spacing));

                            Block block = w.getBlockAt(fangLoc);
                            while (block.getY() > w.getMinHeight() && block.isPassable()) {
                                block = block.getRelative(BlockFace.DOWN);
                            }
                            fangLoc.setY(block.getY() + 1);

                            float yaw = (float) Math.toDegrees(Math.atan2(fangDir.getZ(), fangDir.getX())) - 90;
                            fangLoc.setYaw(yaw);

                            EvokerFangs fangs = (EvokerFangs) w.spawnEntity(fangLoc, EntityType.EVOKER_FANGS);
                            fangs.setOwner(p);
                        }

                        spawned++;
                    }
                }
            }.runTaskTimer(plugin, 0L, delay);
        };

        spawnFangsRunnable.run();
        Bukkit.getScheduler().runTaskLater(plugin, spawnFangsRunnable, 30L);

        evokercooldowns.put(uuid, now);
    }

    @EventHandler
    public void redDragon(EntityShootBowEvent event) {
        NamespacedKey special_effectKey = new NamespacedKey(this.weapons.main, "special_effect");

        ItemStack item = event.getBow();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(special_effectKey, PersistentDataType.STRING)) return;
        if (!Objects.equals(container.get(special_effectKey, PersistentDataType.STRING), "thereddragon")) return;

        if (!(event.getEntity() instanceof Player player)) return;

        event.setCancelled(true);

        for (int i = 0; i < 3; i++) {
            int delay = i * 5;

            Bukkit.getScheduler().runTaskLater(this.weapons.main, () -> {
                Fireball fireball = player.launchProjectile(Fireball.class);
                fireball.setVelocity(player.getLocation().getDirection().multiply(1.5));
                fireball.setIsIncendiary(false);
                fireball.setYield(1.5f);

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
            }, delay);
        }
    }

    @EventHandler
    public void dragonSythe(PlayerInteractEvent event) {
        NamespacedKey specialEffectKey = new NamespacedKey(this.weapons.main, "special_effect");

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!container.has(specialEffectKey, PersistentDataType.STRING)) return;
        if (!Objects.equals(container.get(specialEffectKey, PersistentDataType.STRING), "dragon_sythe")) return;

        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);

            Key dragonSytheKey = Key.key("masworld", "dragon_sythe");

            if (player.getCooldown(dragonSytheKey) == 0) {
                player.getWorld().spawn(player.getEyeLocation(), DragonFireball.class, fb -> {
                    fb.setShooter(player);

                    fb.setVelocity(player.getLocation().getDirection().multiply(2));
                    fb.setDirection(player.getLocation().getDirection().multiply(2));
                });

                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);

                player.setCooldown(dragonSytheKey, 800);
            }
        }
    }

    @EventHandler
    public void gravityStriker(PlayerInteractEvent event) {
        NamespacedKey specialEffectKey = new NamespacedKey(this.weapons.main, "special_effect");

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!"gravity_striker".equals(container.get(specialEffectKey, PersistentDataType.STRING))) return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        Key cooldownKey = Key.key("masworld", "gravity_striker");

        if (player.getCooldown(cooldownKey) > 0) return;

        double pullRadius = 10;
        double pullSpeed = 0.2;
        int durationTicks = 40;

        Location playerLoc = player.getLocation().clone();

        // Keep track of rings
        class Ring {
            double radius;
            int age = 0;
            int lifetime;

            Ring(double radius, int lifetime) {
                this.radius = radius;
                this.lifetime = lifetime;
            }

            void spawnParticles() {
                int points = 30;
                double currentRadius = radius * (1.0 - ((double) age / lifetime));
                for (int i = 0; i < points; i++) {
                    double angle = 2 * Math.PI * i / points;
                    double x = Math.cos(angle) * currentRadius;
                    double z = Math.sin(angle) * currentRadius;
                    Location particleLoc = playerLoc.clone().add(x, 0.1, z);
                    playerLoc.getWorld().spawnParticle(
                            Particle.PORTAL,
                            particleLoc,
                            2,
                            0, 0, 0,
                            0
                    );
                }
                age++;
            }

            boolean isExpired() {
                return age >= lifetime;
            }
        }

        List<Ring> activeRings = new ArrayList<>();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (ticks % 5 == 0) {
                    activeRings.add(new Ring(pullRadius, 20));
                }

                for (Entity entity : player.getNearbyEntities(pullRadius, pullRadius, pullRadius)) {
                    if (entity.equals(player)) continue;
                    if (entity instanceof Player p) {
                        if (p.getGameMode() == GameMode.CREATIVE) {
                            continue;
                        }

                        if (p.getGameMode() == GameMode.SPECTATOR) {
                            continue;
                        }
                    }

                    Vector direction = playerLoc.toVector().subtract(entity.getLocation().toVector());
                    direction.setY(Math.abs(direction.getY()) + 0.2);
                    direction.normalize().multiply(pullSpeed);
                    entity.setVelocity(direction);
                }

                Iterator<Ring> iterator = activeRings.iterator();
                while (iterator.hasNext()) {
                    Ring ring = iterator.next();
                    ring.spawnParticles();
                    if (ring.isExpired()) iterator.remove();
                }

                ticks++;

                if (ticks >= durationTicks) {
                    cancel();
                }
            }
        }.runTaskTimer(this.weapons.main, 0L, 1L);

        player.setCooldown(cooldownKey, 400);
    }

    private final Map<UUID, Location> voidBeam_playerPoints = new HashMap<>();

    @EventHandler
    public void voidBeam(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey specialEffectKey = new NamespacedKey(this.weapons.main, "special_effect");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!"void_beam".equals(container.get(specialEffectKey, PersistentDataType.STRING))) return;

        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        if (!(player.getCooldown(Key.key("masworld", "void_beam")) == 0)) {
            player.sendMessage(TextSymbols.error("Under Cooldown!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            return;
        }

        // Get the block player is looking at within 50 blocks
        Block targetBlock = player.getTargetBlockExact(50);
        if (targetBlock == null) {
            player.sendMessage("No block in sight!");
            return;
        }

        Location targetLocation = targetBlock.getLocation().add(0.5, 0.5, 0.5); // center

        UUID playerId = player.getUniqueId();

        if (!voidBeam_playerPoints.containsKey(playerId)) {
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1f, 1f);
            voidBeam_playerPoints.put(playerId, targetLocation);
            player.getWorld().spawnParticle(
                    Particle.END_ROD,
                    targetLocation,
                    20,
                    0.3, 0.3, 0.3,
                    0.01
            );
            player.sendMessage(TextSymbols.info("First point set!"));
        } else {
            Location start = voidBeam_playerPoints.remove(playerId);
            Location end = targetLocation;

            if (start.distanceSquared(end) <= 0.001) {
                player.sendMessage(TextSymbols.error("Points are too close together."));
                return;
            }

            player.getWorld().spawnParticle(
                    Particle.END_ROD,
                    targetLocation,
                    20,
                    0.3, 0.3, 0.3,
                    0.01
            );

            player.sendMessage(TextSymbols.info("Second point set! Creating beam."));
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1f);
            spawnVoidBeam(start, end, player);
            player.setCooldown(Key.key("masworld", "void_beam"), 500);
        }
    }

    private void spawnVoidBeam(Location start, Location end, Player shooter) {
        World world = start.getWorld();
        if (world == null) return;

        Vector direction = end.toVector().subtract(start.toVector());
        double length = direction.length();
        if (length <= 0.001) {
            shooter.sendMessage(TextSymbols.error("Something went wrong while executing a function."));
            shooter.sendMessage(TextSymbols.error("Points are too close together."));
            return;
        }

        direction.normalize();

        double step = 0.2;
        Set<UUID> alreadyHit = new HashSet<>();

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 20 * 20;

            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    cancel();
                    return;
                }

                for (double i = 0; i <= length; i += step) {
                    Location point = start.clone().add(direction.clone().multiply(i));

                    world.spawnParticle(Particle.PORTAL, point, 3, 0, 0, 0, 0);

                    for (Entity entity : world.getNearbyEntities(point, 1, 1, 1)) {
                        if (!(entity instanceof Damageable target)) continue;

                        if (target.equals(shooter)) continue;
                        if (alreadyHit.contains(target.getUniqueId())) continue;

                        target.damage(2.0, shooter);
                        alreadyHit.add(target.getUniqueId());
                    }
                }

                alreadyHit.clear();
                ticks++;
            }
        }.runTaskTimer(this.weapons.main, 0L, 1L);
    }

    /*
    @EventHandler
    public void entityStringTest(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!(event.isSneaking())) return;

        Entity target = player.getTargetEntity(5);
        if (target == null) return;

        ReadWriteNBT nbt = NBT.parseNBT(Objects.requireNonNull(target.getAsString()));
        nbt.removeKey("Paper.Origin");
        nbt.removeKey("Paper.OriginWorld");
        nbt.removeKey("Rotation");
        nbt.removeKey("Motion");
        nbt.removeKey("FallFlying");
        nbt.removeKey("OnGround");
        nbt.removeKey("FallDistance");
        nbt.removeKey("PortalCooldown");
        nbt.removeKey("Velocity");

        player.sendMessage(nbt.toString());
    }*/

    @EventHandler
    public void enderpholicSword(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey specialEffectKey = new NamespacedKey(this.weapons.main, "special_effect");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!"enderpholic_sword".equals(container.get(specialEffectKey, PersistentDataType.STRING))) return;

        if (!event.getAction().name().contains("RIGHT_CLICK")) return;

        if (!(player.getCooldown(Key.key("masworld", "enderpholic_sword")) == 0)) {
            player.sendMessage(TextSymbols.error("Under Cooldown!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            return;
        }

        Location loc = player.getLocation();
        Vector direction = loc.getDirection().normalize();
        loc.add(direction.multiply(4));
        if (!(loc.getBlock().getType() == Material.AIR)) {
            player.sendMessage(TextSymbols.error("Cannot teleport! Obstruction detected!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            return;
        }

        player.getWorld().spawnParticle(
                Particle.PORTAL,
                loc.getX(), loc.getY(), loc.getZ(),
                20,
                0.4, 0.5, 0.4,
                0.4
        );

        player.getWorld().playSound(loc,Sound.ENTITY_PLAYER_TELEPORT,1,1);

        player.teleport(loc);

        player.setCooldown(Key.key("masworld", "enderpholic_sword"), 200);
    }

    public EnumSet<EntityType> BLACKLIST_VOID_BUNDLE = EnumSet.of(
        EntityType.RAVAGER
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    public void void_bundle_entity_override(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey specialEffectKey = new NamespacedKey(this.weapons.main, "special_effect");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!"void_bundle".equals(container.get(specialEffectKey, PersistentDataType.STRING))) return;

        if (!(player.getCooldown(Key.key("masworld", "void_bundle")) == 0)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void void_bundle_at_entity_override(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey specialEffectKey = new NamespacedKey(this.weapons.main, "special_effect");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!"void_bundle".equals(container.get(specialEffectKey, PersistentDataType.STRING))) return;

        if (!(player.getCooldown(Key.key("masworld", "void_bundle")) == 0)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void void_bundle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey specialEffectKey = new NamespacedKey(this.weapons.main, "special_effect");
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (!"void_bundle".equals(container.get(specialEffectKey, PersistentDataType.STRING))) return;

        if (!(player.getCooldown(Key.key("masworld", "void_bundle")) == 0)) {
            player.sendMessage(TextSymbols.error("Under Cooldown!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
            return;
        }

        event.setCancelled(true);

        NamespacedKey storedkey = new NamespacedKey(this.weapons.main, "void_bundle_entity");

        if (event.getAction().name().contains("RIGHT_CLICK")) {
            if (!Objects.equals(item.getPersistentDataContainer().get(storedkey, PersistentDataType.STRING), "")) {
                player.sendMessage(TextSymbols.error("This void bundle already contains an entity!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                return;
            }

            Entity target = player.getTargetEntity(5);

            if (target == null) {
                player.sendMessage(TextSymbols.error("Your not looking at a entity!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                return;
            };

            if (!(target instanceof LivingEntity living)
                    || living instanceof Player
                    || living instanceof Boss
                    || living.getType() == EntityType.ARMOR_STAND
                    || BLACKLIST_VOID_BUNDLE.contains(target.getType())) {

                player.sendMessage(TextSymbols.error("Cannot store this mob!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                return;
            }

            ReadWriteNBT nbt = NBT.parseNBT(Objects.requireNonNull(target.getAsString()));
            nbt.removeKey("Paper.Origin");
            nbt.removeKey("Paper.OriginWorld");
            nbt.removeKey("Rotation");
            nbt.removeKey("Motion");
            nbt.removeKey("FallFlying");
            nbt.removeKey("OnGround");
            nbt.removeKey("FallDistance");
            nbt.removeKey("PortalCooldown");
            nbt.removeKey("Velocity");

            ItemMeta new_meta = item.getItemMeta();
            if (new_meta == null) return;

            List<Component> lore = meta.lore();
            if (lore == null) lore = new ArrayList<>();

            String entityName = target.getName() + " (" + target.getType().getKey() + ")";

            lore.set(7, Component.text(entityName, NamedTextColor.GRAY));
            new_meta.lore(lore);

            item.setItemMeta(new_meta);

            item.editPersistentDataContainer(pdc -> {
                pdc.set(storedkey, PersistentDataType.STRING, nbt.toString());
            });

            target.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION_OMINOUS,
                target.getX(),target.getY(),target.getZ(),
                30,
                0.3,0.5,0.3,
                0.01
            );

            target.getWorld().playSound(target.getLocation(),Sound.ENTITY_ENDER_EYE_DEATH,1,1);
            target.getWorld().playSound(target.getLocation(),Sound.ITEM_BUNDLE_INSERT,1,1);

            target.remove();

            player.setCooldown(Key.key("masworld", "void_bundle"), 80);
        } else if (event.getAction().name().contains("LEFT_CLICK")) {
            if (Objects.equals(item.getPersistentDataContainer().get(storedkey, PersistentDataType.STRING), "")) {
                player.sendMessage(TextSymbols.error("This void bundle doesnt contain a entity!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                return;
            }

            ReadWriteNBT nbt = NBT.parseNBT(Objects.requireNonNull(item.getPersistentDataContainer().get(storedkey,PersistentDataType.STRING)));

            EntityType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE).get(Utils.parseNamespacedKey(nbt.getString("id")));

            assert type != null;

            Block targetBlock = player.getTargetBlockExact(5);
            if (targetBlock == null) {
                player.sendMessage(TextSymbols.error("You need to be pointing at a block to release your stored entity!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                return;
            }

            Block current = targetBlock;
            Block airBlock = null;

            for (int i = 0; i < 5; i++) {
                current = current.getRelative(BlockFace.UP);

                if (current.getType().isAir()) {
                    airBlock = current;
                    break;
                }
            }

            if (airBlock == null) {
                player.sendMessage(TextSymbols.error("No space above the block!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
                return;
            }

            Entity entity = player.getWorld().spawnEntity(airBlock.getLocation(),type);

            NBT.modify(entity, entity_nbt -> {
                entity_nbt.mergeCompound(nbt);
            });

            entity.getWorld().spawnParticle(Particle.TRIAL_SPAWNER_DETECTION,
                    entity.getX(),entity.getY(),entity.getZ(),
                    30,
                    0.3,0.5,0.3,
                    0.01
            );

            entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_ENDER_EYE_DEATH,1,1);
            entity.getWorld().playSound(entity.getLocation(),Sound.ITEM_BUNDLE_INSERT,1,1);

            ItemMeta new_meta = item.getItemMeta();
            if (new_meta == null) return;

            List<Component> lore = meta.lore();
            if (lore == null) lore = new ArrayList<>();

            lore.set(7, Component.text("Nothing", NamedTextColor.GRAY));
            new_meta.lore(lore);

            item.setItemMeta(new_meta);

            item.editPersistentDataContainer(pdc -> {
                pdc.set(storedkey, PersistentDataType.STRING, "");
            });

            player.setCooldown(Key.key("masworld", "void_bundle"), 80);
        }
    }

    @EventHandler
    public void collectVoidBottle(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) return;
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() != Material.GLASS_BOTTLE) return;
        if (player.getLocation().getBlockY() > 20) return;

        event.setCancelled(true);

        ItemStack voidBottle = CraftEngineItems
                .byId(net.momirealms.craftengine.core.util.Key.of("masworldce", "void_bottle"))
                .buildItemStack()
                .clone();

        if (handItem.getAmount() == 1) {
            player.getInventory().setItemInMainHand(voidBottle);
        } else {
            handItem.setAmount(handItem.getAmount() - 1);

            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(voidBottle);
            if (!leftover.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), voidBottle);
            }
        }

        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1f, 1f);
        player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL, 1f, 1f);
    }
}