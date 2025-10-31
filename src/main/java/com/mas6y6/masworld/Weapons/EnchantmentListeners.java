package com.mas6y6.masworld.Weapons;

import com.mas6y6.masworld.Objects.MasworldTagsSets;
import com.mas6y6.masworld.Objects.Utils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class EnchantmentListeners implements Listener {
    public Weapons weapons;

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 200;

    private final Set<UUID> recentlyDropped = new HashSet<>();
    private final int dropCooldownTicks = 20;


    public EnchantmentListeners(Weapons weapons) {
        this.weapons = weapons;

        Bukkit.getScheduler().runTaskTimer(this.weapons.main,() -> {
            for (Player player : this.weapons.main.getServer().getOnlinePlayers()) {
                ItemMagnet(player);
            }
        }, 0L,20L);

        Bukkit.getScheduler().runTaskTimer(this.weapons.main,this::photosynthisEnchantmentTick, 0L, 20L);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {

        Item dropped = event.getItemDrop();
        recentlyDropped.add(dropped.getUniqueId());

        Bukkit.getScheduler().runTaskLater(this.weapons.main, () -> {
            recentlyDropped.remove(dropped.getUniqueId());
        }, dropCooldownTicks);
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

                        if (!(target.isEmpty())) {
                            meta.setDamage(meta.getDamage() + 1);
                        }
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
    private void shockerEnchantment(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;

        ItemStack weapon = attacker.getEquipment().getItemInMainHand();
        Entity victim = event.getEntity();

        NamespacedKey key = new NamespacedKey("masworld", "shocker");
        Enchantment shockerEnchantment = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(key);

        assert shockerEnchantment != null;
        if (weapon.containsEnchantment(shockerEnchantment)) {
            World world = victim.getLocation().getWorld();

            if (world.hasStorm() && world.isThundering()) {
                world.playSound(victim.getLocation(),Sound.ITEM_TRIDENT_THUNDER, 1.0f, 1.0f);
                world.strikeLightningEffect(victim.getLocation());
            }
        }
    }

    public void photosynthisEnchantmentTick() {
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getWorld().getName().equals("world"))
            .filter(p -> p.getWorld().isDayTime())
            .filter(p -> p.getLocation().getBlock().getLightFromSky() > 0)
            .forEach(this::photosynthesisEnchantmentHandler);
    }

    public void photosynthesisEnchantmentHandler(Player player) {
        EntityEquipment inventory = player.getEquipment();

        NamespacedKey key = new NamespacedKey("masworld", "photosynthesis");
        Enchantment photosynthesisEnchantment = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .getOrThrow(key);

        List<ItemStack> items = List.of(
            inventory.getItemInMainHand(),
            inventory.getItemInOffHand(),
            inventory.getHelmet(),
            inventory.getChestplate(),
            inventory.getLeggings(),
            inventory.getBoots()
        );

        boolean hasPhotosynthesis = items.stream()
                .filter(Objects::nonNull)
                .anyMatch(item -> item.containsEnchantment(photosynthesisEnchantment));

        if (hasPhotosynthesis) {
            player.getWorld().spawnParticle(
                    Particle.HAPPY_VILLAGER,
                    player.getLocation().getX(),
                    player.getLocation().getY(),
                    player.getLocation().getZ(),
                    10,
                    0.2, 0.5, 0.2,
                    10,
                    null,
                    true
            );

            for (ItemStack item : items) {
                if (item != null && item.containsEnchantment(photosynthesisEnchantment)) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof Damageable damageable) {
                        damageable.heal(1);
                        item.setItemMeta((ItemMeta) damageable);
                    }
                }
            }
        }
    }

    @EventHandler
    public void smelterOnBlockBreak(BlockBreakEvent event) {
        NamespacedKey key = new NamespacedKey("masworld", "smelter");
        Enchantment smelterEnchantment = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .getOrThrow(key);

        ItemStack item = event.getPlayer().getEquipment().getItemInMainHand();

        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (item.containsEnchantment(smelterEnchantment)) {
            if (MasworldTagsSets.ORES.contains(block.getType())) {
                event.getPlayer().getNearbyEntities(5,5,5)
                    .stream()
                    .filter(entity -> entity instanceof Item)
                    .map(entity -> (Item) entity)
                    .filter(e -> Utils.getCookedMaterial(e.getItemStack().getType()) != null)
                    .forEach(itemEntity -> {
                        Material cookedMaterial = Utils.getCookedMaterial(itemEntity.getItemStack().getType());
                        assert cookedMaterial != null;
                        ItemStack cooked = new ItemStack(cookedMaterial, itemEntity.getItemStack().getAmount());
                        itemEntity.setItemStack(cooked);
                        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f);
                        itemEntity.getWorld().spawnParticle(Particle.FLAME, itemEntity.getLocation(), 5);
                    });
            }
        }
    }
}
