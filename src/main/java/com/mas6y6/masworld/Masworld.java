package com.mas6y6.masworld;
import com.mas6y6.masworld.Commands.PersonalVault.PersonalVault;
import com.mas6y6.masworld.Commands.XPBottler;
import com.mas6y6.masworld.Economy.MasEconomy;
import com.mas6y6.masworld.ItemEffects.ItemEffects;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mas6y6.masworld.Weapons.Weapons;
import com.mas6y6.masworld.Chat.Chat;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.Configuration;
import java.io.File;
import com.mas6y6.masworld.Weapons.Attributes.Utils.SetWeaponDamage;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public final class Masworld extends JavaPlugin {

    public Configuration config;
    public ItemEffects itemeffects;
    public MasEconomy maseconomy;
    public Weapons weapons;
    public Chat chat;

    public XPBottler xpBottler;
    public PersonalVault personalVault;

    @Override
    public void onEnable() {
        getLogger().info("Starting Masworld Handler");

        SetWeaponDamage.init(this);

        saveDefaultConfig();
        config = getConfig();

        String path = config.getString("items_directory", "items/");
        File dir = new File(path);
        if (!dir.isAbsolute()) {
            dir = new File(getDataFolder(), path);
        }

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                getLogger().severe("Failed to create effect directory at: " + dir.getAbsolutePath());
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        itemeffects = new ItemEffects(this,dir);
        try {
            itemeffects.loadEffects();
        } catch (Exception e) {
            this.getLogger().severe("Failed to get files for item effects." + e.getMessage());
            e.printStackTrace();
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.maseconomy = new MasEconomy(this);

        this.weapons = new Weapons(this);
        this.chat = new Chat(this);

        this.xpBottler = new XPBottler(this);
        this.personalVault = new PersonalVault(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("masworld");

            root.then(Commands.literal("reload").executes(this::pluginReloadcommmand));

            root.then(itemeffects.buildCommands());
            root.then(maseconomy.buildCommands());
            root.then(weapons.buildCommands());

            commands.registrar().register(root.build());

            commands.registrar().register(weapons.buildAdminStickCMD().build());
            commands.registrar().register(this.xpBottler.cmd.build());
        });

        getLogger().info("Registered Commands");

        getServer().getScheduler().runTaskTimer(this,() -> {
            for (Player player : getServer().getOnlinePlayers()) {
                if (player.hasPermission("masworld.itemeffect")) {
                    this.itemeffects.applyEffects(player);
                }
            }
        }, 0L,20L);

        getServer().getPluginManager().registerEvents(new EventsListener(this), this);
    }

    @Override
    public void onDisable() {
        SetWeaponDamage.shutdown();
        getLogger().info("Shutting down Masworld Plugin");
    }

    public ItemEffects getItemEffects() {
        return this.itemeffects;
    }

    public int pluginReloadcommmand(CommandContext context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();
        if (source.getSender() instanceof Player player) {
            player.sendMessage(TextSymbols.WARNING.append(Component.text("Reloading Masworld").color(YELLOW)));
        }

        mainReload();

        if (source.getSender() instanceof Player player) {
            player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Reload Complete!").color(GREEN)));
        }
        return 0;
    }

    public void mainReload() {
        getLogger().info("Reloading Masworld Plugin");
        try {
            this.itemeffects.loadEffects();
        } catch (Exception e) {
            this.getLogger().severe("Error reloading the ItemEffects: " + e.getMessage());
            e.printStackTrace();
        }

        for (Player player : getServer().getOnlinePlayers()) {
            if (player.hasPermission("masworld.itemeffect")) {
                this.itemeffects.applyEffects(player);
            }
        }
        getLogger().info("Reload Complete");
    }

    public MasEconomy getEconomy() {
        return this.maseconomy;
    }
}