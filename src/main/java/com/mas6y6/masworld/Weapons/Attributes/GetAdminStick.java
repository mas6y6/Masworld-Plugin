package com.mas6y6.masworld.Weapons.Attributes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
//import org.bukkit.attribute.Attribute;
//import org.bukkit.attribute.AttributeModifier;
//import org.bukkit.attribute.AttributeModifier.Operation;
//import org.bukkit.plugin.Plugin;
import org.bukkit.enchantments.Enchantment;
import java.lang.*;
import java.util.*;
import com.mas6y6.masworld.Weapons.Attributes.Utils.SetWeaponDamage;

public class GetAdminStick {
    public ItemStack adminStick() {
        String adminStickName = "Admin Stick";
        ItemStack _adminStick = new ItemStack(Material.STICK, 1);
        //_adminStick.addEnchantment(Enchantment.CHANNELING, 1);
        ItemMeta adminStickIM = _adminStick.getItemMeta();
        adminStickIM.setDisplayName(adminStickName);
        adminStickIM.setUnbreakable(true);
        adminStickIM.addEnchant(Enchantment.CHANNELING, 1, true);
        _adminStick.setItemMeta(adminStickIM);
        ItemStack adminStick = SetWeaponDamage.weaponDamage(_adminStick, 10000.0); //no "new" bc function is static
        return adminStick;
    }
}