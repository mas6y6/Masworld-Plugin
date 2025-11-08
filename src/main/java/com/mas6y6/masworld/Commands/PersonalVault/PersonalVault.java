package com.mas6y6.masworld.Commands.PersonalVault;
import com.mas6y6.masworld.Masworld;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import org.bukkit.Bukkit;
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
    }

    public Vault createVault(UUID uuid) {
        File file = new File(this.personalVaultPath.toAbsolutePath().toString(), uuid.toString() + ".db");

        if (!(file.exists())) {
            try {
                file.createNewFile();

                Vault vault = new Vault(file.getPath());
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

        Commands register = event.registrar();
        register.register(base.build());
        register.register(alias1.build());
    }
}
