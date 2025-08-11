package com.mas6y6.masworld.ItemEffects;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Listeners implements Listener {
    private final ItemEffects itemeffects;
    private final Map<UUID, Long> lastEffectApply = new HashMap<>();
    private static final long COOLDOWN_MS = 50;

    public Listeners(ItemEffects main) {
        this.itemeffects = main;
    }

    private boolean shouldApply(UUID playerId) {
        long now = System.currentTimeMillis();
        return lastEffectApply.getOrDefault(playerId, 0L) + COOLDOWN_MS < now;
    }

    private void tryApply(Player player) {
        if (player.hasPermission("masworld.itemeffects")) {
            if (shouldApply(player.getUniqueId())) {
                this.itemeffects.applyEffects(player);
                lastEffectApply.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onInventorySlotChange(PlayerInventorySlotChangeEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Apply effects on any click in player inventory for reliability
        if (event.getInventory().getType() == InventoryType.PLAYER) {
            tryApply(player);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getInventory().getType() == InventoryType.PLAYER) {
            tryApply(player);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onEntityEquipmentChange(EntityEquipmentChangedEvent event) {
        if (event.getEntity() instanceof Player player) {
            tryApply(player);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onInventoryPickup(PlayerPickItemEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        tryApply(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        tryApply(event.getPlayer());
    }
}
