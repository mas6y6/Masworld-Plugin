package com.mas6y6.masworld.Commands.PersonalVault;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PersonalVaultListener implements Listener {
    public PersonalVault personalVault;

    public PersonalVaultListener(PersonalVault personalVault) {
        this.personalVault = personalVault;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.personalVault.createVault(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.personalVault.closeVault(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof VaultInventoryHolder holder) {

            Map<Integer, ItemStack> items = new HashMap<>();
            Inventory inv = event.getInventory();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item != null) items.put(i, item);
            }

            holder.getVault().saveItems(items);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof VaultInventoryHolder holder) {

            if (event.getClickedInventory() != event.getView().getTopInventory()) {
                event.setCancelled(true);
            }

            if (event.getClick().isShiftClick()) {
                event.setCancelled(true);
            }
        }
    }

}
