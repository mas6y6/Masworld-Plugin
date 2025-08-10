package com.mas6y6.masworld.ItemEffects.Objects;

import java.util.*;

public class EffectRegister {
    public int cfgver;
    public String id;
    public String name;
    public boolean disabled;
    public boolean onlysneaking;
    public List<String> dimensions;
    public List<String> slots;
    public Map<String, EffectObject> effects;
    public transient String path;

    public boolean validate() {
        if (cfgver == 0) throw new IllegalStateException("cfgver cannot be 0");
        if (id == null || id.isEmpty()) throw new IllegalStateException("id cannot be null or empty");
        if (name == null || name.isEmpty()) throw new IllegalStateException("name cannot be null or empty");
        // onlysneaking is primitive boolean, can't be null, no need to check
        if (dimensions == null || dimensions.isEmpty()) throw new IllegalStateException("dimensions cannot be null or empty");
        if (slots == null || slots.isEmpty()) throw new IllegalStateException("slots cannot be null or empty");
        if (effects == null || effects.isEmpty()) throw new IllegalStateException("effects cannot be null or empty");

        // Optional: validate entries inside effects
        for (Map.Entry<String, EffectObject> entry : effects.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isEmpty()) {
                throw new IllegalStateException("effects contains null or empty key");
            }
            if (entry.getValue() == null) {
                throw new IllegalStateException("effects contains null EffectObject");
            }
        }

        return true;
    }


    public int getCfgver() {
        return cfgver;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isOnlySneaking() {
        return onlysneaking;
    }

    public List<String> getDimensions() {
        return dimensions;
    }

    public Map<String, EffectObject> getEffects() {
        return effects;
    }

    public List<String> getSlots() {
        return slots;
    }

    public String getPath() {
        return path;
    }

    public EffectRegister copy() {
        EffectRegister copy = new EffectRegister();

        copy.cfgver = this.cfgver;
        copy.id = this.id;
        copy.name = this.name;
        copy.disabled = this.disabled;
        copy.onlysneaking = this.onlysneaking;
        copy.path = this.path;

        // Deep copy lists
        copy.dimensions = (this.dimensions != null) ? new ArrayList<>(this.dimensions) : null;
        copy.slots = (this.slots != null) ? new ArrayList<>(this.slots) : null;

        if (this.effects != null) {
            Map<String, EffectObject> effectsCopy = new HashMap<>();
            for (Map.Entry<String, EffectObject> entry : this.effects.entrySet()) {
                EffectObject originalEffect = entry.getValue();
                if (originalEffect != null) {
                    EffectObject effectCopy = new EffectObject();
                    effectCopy.amplifier = originalEffect.amplifier;
                    effectCopy.priority = originalEffect.priority;
                    effectsCopy.put(entry.getKey(), effectCopy);
                }
            }
            copy.effects = effectsCopy;
        }

        return copy;
    }
}