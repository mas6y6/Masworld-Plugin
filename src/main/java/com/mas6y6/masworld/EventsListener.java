package com.mas6y6.masworld;

import com.mas6y6.masworld.Objects.CraftEngineUtils;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class EventsListener implements Listener {
    public Masworld masworld;

    public EventsListener(Masworld plugin) {
        this.masworld = plugin;
    }
}
