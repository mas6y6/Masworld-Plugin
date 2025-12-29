package com.mas6y6.masworld.Items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.lang.*;

import com.mas6y6.masworld.Items.Attributes.Utils.SetWeaponDamage;

public class GetAdminStick {
    public ItemStack adminStick() {
        String adminStickName = "Admin Stick";
        ItemStack _adminStick = ItemType.STICK.createItemStack();
        _adminStick.setAmount(1);
        ItemMeta adminStickIM = _adminStick.getItemMeta();
        //NamespacedKey key = new NamespacedKey("masworld", "adminstick_id");
        NamespacedKey key = new NamespacedKey("masworld", "adminstick")
        PersistentDataContainer container = adminStickIM.getPersistentDataContainer();
        container.set(key, PersistentDataType.BOOLEAN, true);
        AttackRangeComponent adminStickIM_AR = adminStickIM.getAttackRange();
        if (adminStickIM_AR == null) {
            adminStickIM_AR = adminStickIM.getComponents().getAttackRange();
        }
        adminStickIM_AR.setValue(100);
        adminStickIM.setAttackRange(adminStickIM_AR);
        adminStickIM.setDisplayName(adminStickName);
        adminStickIM.setUnbreakable(true);
        adminStickIM.addEnchant(Enchantment.CHANNELING, 1, true);
        _adminStick.setItemMeta(adminStickIM);
        ItemStack adminStick = SetWeaponDamage.weaponDamage(_adminStick, 10000.0); //no "new" bc function is static
        return adminStick;
    }
}