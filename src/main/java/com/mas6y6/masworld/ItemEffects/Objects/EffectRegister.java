package com.mas6y6.masworld.ItemEffects.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "cfgver", "id", "name", "disabled", "onlysneaking", "dimensions", "slots", "effects" })
public class EffectRegister {
    public int cfgver;
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("disabled")
    public boolean disabled;
    @JsonProperty("onlysneaking")
    public boolean onlysneaking;
    @JsonProperty("dimensions")
    public List<String> dimensions;
    @JsonProperty("slots")
    public List<String> slots;
    @JsonProperty("effects")
    public Map<String, EffectData> effects;
    public transient String path;

    public boolean validate() {
        if (cfgver == 0) throw new IllegalStateException("cfgver cannot be 0");
        if (id == null || id.isEmpty()) throw new IllegalStateException("id cannot be null");
        if (name == null || name.isEmpty()) throw new IllegalStateException("name cannot be null");
        // onlysneaking is primitive boolean, can't be null, no need to check
        if (dimensions == null) throw new IllegalStateException("dimensions cannot be null");
        if (slots == null) throw new IllegalStateException("slots cannot be null");
        if (effects == null) throw new IllegalStateException("effects cannot be null");

        // Optional: validate entries inside effects
        for (Map.Entry<String, EffectData> entry : effects.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isEmpty()) {
                throw new IllegalStateException("effects contains null or empty key");
            }
            if (entry.getValue() == null) {
                throw new IllegalStateException("effects contains null EffectObject");
            }
        }

        return true;
    }


    @JsonProperty("cfgver")
    public int getCfgver() {
        return cfgver;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("disabled")
    public boolean isDisabled() {
        return disabled;
    }

    @JsonProperty("onlysneaking")
    public boolean isOnlySneaking() {
        return onlysneaking;
    }

    @JsonProperty("dimensions")
    public List<String> getDimensions() {
        return dimensions;
    }

    @JsonProperty("effects")
    public Map<String, EffectData> getEffects() {
        return effects;
    }

    @JsonProperty("slots")
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
            Map<String, EffectData> effectsCopy = new HashMap<>();
            for (Map.Entry<String, EffectData> entry : this.effects.entrySet()) {
                EffectData originalEffect = entry.getValue();
                if (originalEffect != null) {
                    EffectData effectCopy = new EffectData();
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