package com.mas6y6.masworld.ItemEffects;

import com.mas6y6.masworld.ItemEffects.Objects.EffectRegister;
import com.mas6y6.masworld.Masworld;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ItemEffects {
    private final Masworld main;
    public Map<String, EffectRegister> effects = new HashMap<>();
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("itemeffects");
    public PlayerHandler playerhandler = new PlayerHandler();

    public ItemEffects(Masworld main) {
        this.main = main;
    }

    public void loadEffects(File dir) throws IOException {
        effects = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for (File file : dir.listFiles()) {
            try {
                EffectRegister effectregister = mapper.readValue(file, EffectRegister.class);
                effectregister.path = file.getPath();
                main.getLogger().info("Registered \""+effectregister.name+"\" at "+"\""+effectregister.id+"\"");
                effects.put(effectregister.id,effectregister);
            } catch(Exception e) {
                this.main.getLogger().severe("Error reading \"" + file.getName() + "\": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public EffectRegister getEffect(String id) {
        return this.effects.get(id);
    }

    public void registerEffect(EffectRegister effect) {
        this.effects.put(effect.id, effect);
        main.getLogger().info("Registered \""+effect.name+"\" at "+"\""+effect.id+"\"");
    }

    public void modifyEffect(String id, EffectRegister effect) {
        this.effects.replace(id, effect);
        main.getLogger().info("Modified \""+effect.name+"\"");
    }

    public List<EffectRegister> getAllEffects() {
        return new ArrayList<>(effects.values());
    }
}
