package com.mas6y6.masworld.EconomySystem;

import com.mas6y6.masworld.Masworld;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomySystem {
    private final Masworld main;
    private Economy econ;
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("economy");

    public EconomySystem(Masworld masworld) {
        this.main = masworld;
        if (!setupEconomy()) {
            main.getLogger().severe("Vault or an economy plugin not found! Disabling plugin...");
            main.getServer().getPluginManager().disablePlugin(main);
            return;
        }
        main.getLogger().info("Economy hooked successfully!");
    }

    private boolean setupEconomy() {
        if (main.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }
}
