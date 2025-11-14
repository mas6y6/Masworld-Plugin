package com.mas6y6.masworld.Commands.PersonalVault;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class VaultInventoryHolder implements InventoryHolder {
    private final UUID uuid;
    private final Vault vault; // your database wrapper
    private Inventory inv;

    public VaultInventoryHolder(UUID uuid, Vault vault) {
        this.uuid = uuid;
        this.vault = vault;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Vault getVault() {
        return vault;
    }

    public void setInventory(Inventory inv) {
        this.inv = inv;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
