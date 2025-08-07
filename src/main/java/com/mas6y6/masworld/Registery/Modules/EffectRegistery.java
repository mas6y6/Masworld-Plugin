package com.mas6y6.masworld.Registery.Modules;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class EffectRegistery {
    private String name;
    private int id;
    private ItemStack item;

    private boolean onlysneaking;
    private ArrayList<String> onlydimensions;

    private Map<String, EffectData> effects;
    private ArrayList<String> slots;

    public EffectRegistery(
            String name,
            Integer id,
            ItemStack item,
            boolean onlysneaking,
            ArrayList<String> onlydimensions,
            Map<String, EffectData> effects,
            ArrayList<String> slots) {
        this.name = name;
        this.id = id;
        this.item = item;
        this.onlysneaking = onlysneaking;
        this.onlydimensions = onlydimensions;
        this.effects = effects;
        this.slots = slots;
    }
}