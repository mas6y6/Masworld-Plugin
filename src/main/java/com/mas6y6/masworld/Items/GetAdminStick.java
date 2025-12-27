package com.mas6y6.masworld.Items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import java.lang.*;

import com.mas6y6.masworld.Items.Attributes.Utils.SetWeaponDamage;

public class GetAdminStick {
    public ItemStack adminStick() {
        String adminStickName = "Admin Stick";
        ItemStack _adminStick = ItemType.STICK.createItemStack();
        _adminStick.setAmount(1);

        ItemMeta adminStickIM = _adminStick.getItemMeta();
        adminStickIM.setDisplayName(adminStickName);
        adminStickIM.setUnbreakable(true);
        adminStickIM.addEnchant(Enchantment.CHANNELING, 1, true);
        _adminStick.setItemMeta(adminStickIM);
        ItemStack adminStick = SetWeaponDamage.weaponDamage(_adminStick, 10000.0); //no "new" bc function is static
        return adminStick;
    }
}