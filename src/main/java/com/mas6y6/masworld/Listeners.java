package com.mas6y6.masworld;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Listeners implements Listener {
    private final Masworld main;

    public Listeners(Masworld main) {
        this.main = main;
    }

    @EventHandler
    public void armorHandler(PlayerArmorChangeEvent event) {
        NamespacedKey playerkey = new NamespacedKey(main, "masworld_effects");
        NamespacedKey effectkey = new NamespacedKey(main, "masworld_effect");

        Player player = event.getPlayer();
        PersistentDataContainer player_container = player.getPersistentDataContainer();

        player.sendMessage(Component.text("Found"));
        player.getUniqueId();

        if (event.getOldItem() != null && event.getOldItem().hasItemMeta()) {
            PersistentDataContainer containerOld = event.getOldItem().getItemMeta().getPersistentDataContainer();
            if (containerOld.has(effectkey, PersistentDataType.INTEGER)) {
                Integer id = containerOld.get(effectkey, PersistentDataType.INTEGER);
                if (id != null) {
                    List<Integer> list = player_container.get(playerkey, PersistentDataType.LIST.integers());

                    if (list != null) {
                        list.remove(id);
                        player_container.set(playerkey, PersistentDataType.LIST.integers(), list);
                        player.sendMessage(Component.text("Removed effect " + id));
                    }
                }
            }
        }

        if (event.getNewItem() != null && event.getNewItem().hasItemMeta()) {
            PersistentDataContainer containerNew = event.getNewItem().getItemMeta().getPersistentDataContainer();
            if (containerNew.has(effectkey, PersistentDataType.INTEGER)) {
                Integer id = containerNew.get(effectkey, PersistentDataType.INTEGER);
                if (id != null) {
                    // Get or create a new list
                    List<Integer> list = player_container.get(playerkey, PersistentDataType.LIST.integers());
                    if (list == null) list = new ArrayList<>();

                    if (!list.contains(id)) {
                        list.add(id);
                        player_container.set(playerkey, PersistentDataType.LIST.integers(), list);
                        player.sendMessage(Component.text("Applied effect " + id));
                    }
                }
            }
        }
    }
}