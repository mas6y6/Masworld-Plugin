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
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import com.mas6y6.masworld.Items.GetAdminStick.GetAdminStick;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.Attribute;

import java.util.List;
import java.util.Objects;

public class EventsListener implements Listener {
    public Masworld masworld;

    public EventsListener(Masworld plugin) {
        this.masworld = plugin;
    }

    private final NamespacedKey cekey = new NamespacedKey("masworld", "ce_item");
    private final NamespacedKey countKey = new NamespacedKey("masworld", "item_count");

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        InventoryHolder holder = event.getInventoryHolder();
        if (!(holder instanceof Chest || holder instanceof Barrel)) return;

        List<ItemStack> loot = event.getLoot(); // get the loot list
        for (int i = 0; i < loot.size(); i++) {
            ItemStack item = loot.get(i);
            if (item == null) continue;

            String id = item.getPersistentDataContainer().get(cekey, PersistentDataType.STRING);
            int itemCount = item.getPersistentDataContainer()
                    .getOrDefault(countKey, PersistentDataType.BYTE, (byte) 1);
            if (id == null) continue;

            String[] idParts = id.split(":");
            if (idParts.length != 2) continue;

            CustomItem<ItemStack> customItem = CraftEngineItems.byId(
                    CraftEngineUtils.generateKey(idParts[0], idParts[1])
            );
            if (customItem == null) continue;

            ItemStack newItem = customItem.buildItemStack();
            newItem.setAmount(itemCount);

            loot.set(i, newItem);
        }
    }
    /*
    @EventHandler
    public void onAdminStickEquipped(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack adminstick = GetAdminStick.adminStick();
        ItemStack hand = player.getInventory().getItemInMainHand();
        ItemMeta handIM = hand.getItemMeta();
        NamespacedKey key = new NamespacedKey("masworld", "adminstick_id");
        PersistentDataContainer handIM_PDC = handIM.getPersistentDataContainer();
        if (hand != null &&  hand.getType() != Material.AIR) {
            if (hand.getType() == adminstick.getType() && handIM_PDC.has(key, PersistentDataType.STRING)) {
                handIM.setAttackRange();
            }
        }
    }
    */
}
