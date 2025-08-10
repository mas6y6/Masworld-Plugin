package com.mas6y6.masworld.ItemEffects.Objects;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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