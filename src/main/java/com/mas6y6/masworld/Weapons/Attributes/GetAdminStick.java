package com.mas6y6.masworld.Weapons.Attributes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import java.lang.*;
import java.util.*;
import com.mas6y6.masworld.Weapons.Attributes.Utils.SetWeaponDamage;

public class GetAdminStick {
    private String adminStickName = "Admin Stick";
    ItemStack _adminStick = new ItemStack(Material.STICK, 1);
    adminStick.addEnchantment(Enchantment.CHANNELING, 1);
    ItemMeta adminStickIM = _adminstick.getItemMeta();
    adminStickIM.setDisplayName(adminStickName);
    adminStickIM.setUnbreakable(True);
    _adminStick.setItemMeta(adminStickIM);
    ItemStack adminStick = SetWeaponDamage(_adminStick, 10000);

    public void GetAdminStick() {
        return adminStick;
    }
}