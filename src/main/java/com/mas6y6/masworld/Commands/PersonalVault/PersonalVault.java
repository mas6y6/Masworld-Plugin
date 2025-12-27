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

    public PersonalVault(Masworld plugin) {
        this.main = plugin;

        this.personalVaultPath = this.main.getDataFolder().toPath().resolve("PersonalVault");

        try {
            Files.createDirectories(this.personalVaultPath);
        } catch (IOException e) {
            this.main.getLogger().severe("Could not create PersonalVault directory!");
            throw new RuntimeException(e);
        }

        this.main.getServer().getPluginManager().registerEvents(new PersonalVaultListener(this), this.main);
    }
}
