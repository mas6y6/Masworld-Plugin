package com.mas6y6.masworld.Economy;

import com.mas6y6.masworld.Masworld;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.RegisteredServiceProvider;

public class MasEconomy {
    private static net.milkbowl.vault.economy.Economy econ = null;
    public Masworld main;
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("economy");

    public MasEconomy(Masworld masworld) {
        this.main = masworld;

        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = this.main.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            throw new IllegalStateException("Economy not activated!");
        }

        econ = rsp.getProvider();


    }

    public net.milkbowl.vault.economy.Economy getEconomy() {
        return econ;
    }

    public LiteralArgumentBuilder<CommandSourceStack> buildCommands() {


        return commands;
    }
}
