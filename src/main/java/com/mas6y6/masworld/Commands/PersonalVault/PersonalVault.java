package com.mas6y6.masworld.Commands.PersonalVault;
import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PersonalVault {
    public Masworld main;
    public Path personalVaultPath;
    public Map<UUID, Vault> vaults;

    public PersonalVault(Masworld plugin) {
        this.main = plugin;

        this.personalVaultPath = this.main.getDataFolder().toPath().resolve("PersonalVault");
        this.vaults = new HashMap<UUID, Vault>();
        try {
            Files.createDirectories(this.personalVaultPath);
        } catch (IOException e) {
            this.main.getLogger().severe("Could not create PersonalVault directory!");
            throw new RuntimeException(e);
        }

        this.main.getServer().getPluginManager().registerEvents(new PersonalVaultListener(this), this.main);
    }

    public Vault createVault(UUID uuid) {
        File file = new File(this.personalVaultPath.toAbsolutePath().toString(), uuid.toString() + ".db");

        if (!(file.exists())) {
            try {
                file.createNewFile();

                Vault vault = new Vault(file.getPath(), uuid);
                vaults.put(uuid, vault);

                return vault;
            } catch (Exception e) {
                this.main.getLogger().severe("Could not create PersonalVault file!");
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    public void closeVault(UUID uuid) {
        try {
            this.vaults.get(uuid).close();
        } catch (Exception e) {
            this.main.getLogger().severe("Could not close PersonalVault file! Force removing without closing!");
            this.main.getLogger().severe(e.getMessage());
        }

        this.main.getLogger().info("Closed "+Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName()+"'s PersonalVault");
        this.vaults.remove(uuid);
    }

    public void registerCommands(ReloadableRegistrarEvent<@NotNull Commands> event) {
        LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("personalvault");

        LiteralArgumentBuilder<CommandSourceStack> alias1 = Commands.literal("pv");
        alias1.redirect(base.build());

        LiteralArgumentBuilder<CommandSourceStack> test = Commands.literal("pvtest");
        test.executes(ctx -> {
            CommandSourceStack source = ctx.getSource();
            if (!(source.getSender() instanceof Player player)) {
                // send error
                return 0;
            }

            this.openVault(player);
            return 1;
        });

        Commands register = event.registrar();
        register.register(base.build());
        register.register(alias1.build());
        register.register(test.build());
    }

    public Inventory openVault(Player player) {
        UUID uuid = player.getUniqueId();
        Vault vault = vaults.get(uuid);

        int size = vault.getMaxSlots(); // read from meta table

        VaultInventoryHolder holder = new VaultInventoryHolder(uuid, vault);
        Inventory inv = Bukkit.createInventory(holder, size, Component.text("Personal Vault"));
        holder.setInventory(inv);

        // Load items from SQLite and put into inventory
        Map<Integer, ItemStack> storedItems = vault.loadItems();
        storedItems.forEach(inv::setItem);

        player.openInventory(inv);
        return inv;
    }

}
