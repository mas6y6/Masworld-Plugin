package com.mas6y6.masworld.Weapons.Attributes.Utils;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.UUID;

public class SetItemDamage {
    public static SetItemDamage(ItemStack item, double amount) {
        ItemMeta itemmeta = item.getItemMeta();

        AttributeModifier modifier = new AttributeModifier(
            UUID.randomUUID(),
            "generic.attack_damage",
            amount,
            Operation.ADD_AMOUNT,
            Attribute.ATTACK_DAMAGE
        );
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
        item.setItemMeta(meta);
        return item;
    }
}