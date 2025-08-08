package com.mas6y6.masworld.ItemEffects.Objects;
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
}