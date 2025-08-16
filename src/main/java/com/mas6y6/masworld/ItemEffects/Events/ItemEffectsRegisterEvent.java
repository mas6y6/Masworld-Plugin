package com.mas6y6.masworld.ItemEffects.Events;


import com.mas6y6.masworld.ItemEffects.ItemEffects;
import com.mas6y6.masworld.ItemEffects.Objects.EffectRegister;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ItemEffectsRegisterEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemEffects itemEffects;

    public ItemEffectsRegisterEvent(ItemEffects registry) {
        this.itemEffects = registry;
    }

    public ItemEffects getItemEffects() {
        return itemEffects;
    }

    public void register(EffectRegister effectRegister) {
        effectRegister.pluginloaded = true;
        itemEffects.registerEffect(effectRegister);
    }

    public EffectRegister createEffectRegister() {
        return new EffectRegister();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
