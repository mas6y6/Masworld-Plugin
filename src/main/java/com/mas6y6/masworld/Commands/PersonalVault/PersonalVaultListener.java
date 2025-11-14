package com.mas6y6.masworld.Commands.PersonalVault;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof VaultInventoryHolder inventory) {
            inventory
        }
    }
}
