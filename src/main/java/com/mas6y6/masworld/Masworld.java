package com.mas6y6.masworld;
import com.mas6y6.masworld.Commands.FixItems;
import com.mas6y6.masworld.ItemEffects.ItemEffects;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.function.Function;

import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class Masworld extends JavaPlugin {

    public Configuration config;
    public File itemsDir;
    public ItemEffects itemeffects;

    @Override
    public void onEnable() {
        getLogger().info("Starting Masworld Handler");

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

        FixItems fixitemsfunctions = new FixItems(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("masworld");

            root.then(Commands.literal("reload").executes(this::pluginReload));

            LiteralArgumentBuilder<CommandSourceStack> fixitem = Commands.literal("fixitem");
            fixitem.then(Commands.literal("effect").executes(fixitemsfunctions::effect));
            fixitem.then(Commands.literal("upgrade_template").executes(fixitemsfunctions::upgrade_template));
            fixitem.then(Commands.literal("upgrade_data").executes(fixitemsfunctions::upgrade_data));
            fixitem.then(Commands.literal("recipe_item").executes(fixitemsfunctions::recipe_item));
            fixitem.then(Commands.literal("test").executes(context -> {

                CommandSourceStack source = (CommandSourceStack) context.getSource();
                if (!(source.getSender() instanceof Player player)) {
                    source.getSender().sendMessage(Component.text("Must be a player!").color(RED));
                    return 0;
                }

                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand.isEmpty()) {
                    player.sendMessage(Component.text("Please hold an item.").color(RED));
                    return 0;
                }

                int tier = NBT.get(hand, (Function<ReadableItemNBT, Integer>) nbt -> nbt.getOrDefault("masworld_effect", 0));

                player.sendMessage(
                        Component.text("EffectID = " + tier).color(GREEN)
                );
                return 1;
            }));

            root.then(fixitem);

            root.then(itemeffects.buildCommands());
            commands.registrar().register(root.build());
        });

        getLogger().info("Registered Commands");

        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        getLogger().info("Registered Listeners");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down Masworld Plugin");
    }

    public int pluginReload(CommandContext context) {
        CommandSourceStack source = (CommandSourceStack) context.getSource();
        getLogger().info("Reloading Masworld Plugin");
        if (source.getSender() instanceof Player player) {
            player.sendMessage(TextSymbols.WARNING.append(Component.text("Masworld Plugin reload in progress. Please wait.")));
        }

        try {
            this.itemeffects.loadEffects();
        } catch (Exception e) {
            this.getLogger().severe("Error reloading the ItemEffects: " + e.getMessage());
            e.printStackTrace();
        }

        getLogger().info("Reload Complete");
        if (source.getSender() instanceof Player player) {
            player.sendMessage(TextSymbols.SUCCESS.append(Component.text("Reload Complete!")));
        }
        return 0;
    }
}