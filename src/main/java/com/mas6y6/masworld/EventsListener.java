package com.mas6y6.masworld;

import com.mas6y6.masworld.Objects.CraftEngineUtils;
import com.mas6y6.masworld.Objects.Utils;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EventsListener implements Listener {
    public Masworld masworld;

    public EventsListener(Masworld plugin) {
        this.masworld = plugin;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        if (!(event.getInventoryHolder() instanceof BlockState)) return;

        List<ItemStack> loot = event.getLoot();

        NamespacedKey key = new NamespacedKey("masworld", "loottable_id");

        for (int i = 0; i < loot.size(); i++) {
            ItemStack item = loot.get(i);
            if (item == null) continue;
            if (item.getType() != Material.KNOWLEDGE_BOOK) continue;

            String id = item.getPersistentDataContainer().get(key, PersistentDataType.STRING);

            if (id != null) {
                CustomItem<ItemStack> customItem = CraftEngineItems.byId(
                        CraftEngineUtils.generateKey("masworldce", id)
                );

                assert customItem != null;
                loot.set(i, customItem.buildItemStack());
            }
        }
    }
}
