package com.mas6y6.masworld.ItemEffects.Objects;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectObject {
    public int amplifier;
    public int priority;
    public transient String effectid;
    public transient PotionEffectType effecttype;

    public int getPriority() {
        return this.priority;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    public String getEffectid() {
        if (effecttype == null) {
            throw new IllegalStateException("Effect id is not set for effect: " + effectid);
        }
        return this.effectid;
    }

    public PotionEffectType getEffecttype() {
        if (effecttype == null) {
            throw new IllegalStateException("Effect type is not set for effect: " + effectid);
        }
        return this.effecttype;
    }

    public PotionEffect builtPotion() {
        if (effecttype == null) {
            throw new IllegalStateException("Effect type is not set for effect: " + effectid);
        }

        int defaultDurationTicks = 20 * 60;

        return new PotionEffect(effecttype, defaultDurationTicks, amplifier);
    }
}
