package com.mas6y6.masworld.ItemEffects.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "amplifier", "priority" })
public class EffectData {
    public int amplifier;
    public int priority;

    @JsonIgnore
    public transient String effectid;
    @JsonIgnore
    public transient PotionEffectType effecttype;

    public int getPriority() {
        return this.priority;
    }

    public int getAmplifier() {
        return this.amplifier;
    }

    @JsonIgnore
    public String getEffectid() {
        if (effecttype == null) {
            throw new IllegalStateException("Effect id is not set for effect: " + effectid);
        }
        return this.effectid;
    }

    @JsonIgnore
    public PotionEffectType getEffecttype() {
        if (effecttype == null) {
            throw new IllegalStateException("Effect type is not set for effect: " + effectid);
        }
        return this.effecttype;
    }

    public PotionEffect buildPotion() {
        if (effecttype == null) {
            throw new IllegalStateException("Effect type is not set for effect: " + effectid);
        }

        int infiniteTicks = Integer.MAX_VALUE;

        return new PotionEffect(effecttype, infiniteTicks, amplifier, false, false, false);
    }
}
