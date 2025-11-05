package com.mas6y6.masworld.Commands.PersonalVault;
import com.mas6y6.masworld.Masworld;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PersonalVault {
    public Masworld main;
    public Path personalVaultPath;
    public LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("personalvault");

    public PersonalVault(Masworld plugin) {
        this.main = plugin;

        this.personalVaultPath = this.main.getDataFolder().toPath().resolve("PersonalVault");
        try {
            Files.createDirectories(this.personalVaultPath);
        } catch (IOException e) {
            this.main.getLogger().severe("Could not create PersonalVault directory!");
            throw new RuntimeException(e);
        }
    }

}
