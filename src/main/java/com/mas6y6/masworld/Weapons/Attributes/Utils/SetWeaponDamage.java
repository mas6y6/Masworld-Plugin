package com.mas6y6.masworld.Weapons.Attributes.Utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import java.lang.*;

public class SetWeaponDamage {
    //private static AttributeModifier modifier;
    private static Plugin plugin;

    public static void init(Plugin p) {
        plugin = p;
    }

    public static void shutdown() {
        plugin = null;
    }

    private static void ensureinit() {
        if (plugin == null) {
            throw new IllegalStateException("SetWeaponDamage not initialized. Use SetWeaponDamage.init(Plugin p) in OnEnable");
        }
    }

    public static ItemStack weaponDamage(ItemStack item, double amount) {
        ensureinit();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) { return item; }
        NamespacedKey key = new NamespacedKey(plugin, "admin_stick_damage");
        AttributeModifier modifier = new AttributeModifier(
            key,
            amount,
            Operation.ADD_NUMBER
        );
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, modifier);
        item.setItemMeta(meta);
        return item;
    }
}