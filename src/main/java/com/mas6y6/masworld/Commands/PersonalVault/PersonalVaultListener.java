package com.mas6y6.masworld.Commands.PersonalVault;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PersonalVaultListener implements Listener {
    public PersonalVault personalVault;

    public PersonalVaultListener(PersonalVault personalVault) {
        this.personalVault = personalVault;
    }
}
