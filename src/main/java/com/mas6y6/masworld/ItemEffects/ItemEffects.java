package com.mas6y6.masworld.ItemEffects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mas6y6.masworld.ItemEffects.Events.ItemEffectsRegisterEvent;
import com.mas6y6.masworld.ItemEffects.Objects.EffectData;
import com.mas6y6.masworld.ItemEffects.Objects.EffectRegister;
import com.mas6y6.masworld.ItemEffects.Objects.FunctionCommands;
import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ItemEffects {
    public Masworld main;
    public Map<String, EffectRegister> effects = new HashMap<>();
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("itemeffects");

    public File dir;
    public FunctionCommands functioncommands;

    public Integer cfgver = 1;

    public ItemEffects(Masworld main, File directory) {
        this.main = main;
        this.dir = directory;
        this.functioncommands = new FunctionCommands(this);

        main.getServer().getPluginManager().registerEvents(new Listeners(this), this.main);
    }

    public void loadEffects() {
        effects = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        File[] files = dir.listFiles();
        if (files == null) {
            files = new File[0];  // empty array of File
        }

        // Call any plugins using ItemEffects to register effects
        Bukkit.getPluginManager().callEvent(new ItemEffectsRegisterEvent(this));

        for (File file : files) {
            try {
                EffectRegister effectregister = mapper.readValue(file, EffectRegister.class);
                effectregister.path = file.getPath();

                for (Map.Entry<String, EffectData> entry : effectregister.effects.entrySet()) {
                    String key = entry.getKey();
                    EffectData value = entry.getValue();
                    value.effectid = key;

                    NamespacedKey nsKey = Utils.parseNamespacedKey(key);

                    value.effecttype = Registry.MOB_EFFECT.getOrThrow(nsKey); // Will always supply PotionEffectType since the registry is ```Registry<PotionEffectType>```
                }

                if (effectregister.validate()) {
                    this.registerEffect(effectregister);
                }
            } catch (Exception e) {
                main.getLogger().severe(String.format("Failed to register effect from file \"%s\": %s", file.getName(), e.getMessage()));
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

    public List<String> getEffectIds() {
        return new ArrayList<>(effects.keySet());
    }

    public List<EffectRegister> getAllEffects() {
        return new ArrayList<>(effects.values());
    }

    public String getEffectIdAsString(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(main, "masworld_effect");

        try {
            Integer intVal = pdc.get(key, PersistentDataType.INTEGER);
            if (intVal != null) return intVal.toString();
        } catch (IllegalArgumentException ignored) {
            // Expected if tag type is not integer
        }

        return pdc.get(key, PersistentDataType.STRING);
    }


    public List<EffectData> calculateEffects(Player player) {
        PlayerInventory inventory = player.getInventory();

        Map<String, ItemStack> slotItems = Map.of(
                "helmet", inventory.getHelmet(),
                "chestplate", inventory.getChestplate(),
                "leggings", inventory.getLeggings(),
                "boots", inventory.getBoots(),
                "mainhand", inventory.getItemInMainHand(),
                "offhand", inventory.getItemInOffHand()
        );

        return slotItems.entrySet().stream()
            .filter(entry -> entry.getValue() != null && entry.getValue().getType() != Material.AIR) // skip empty/null
            .map(entry -> {
                String slotName = entry.getKey();
                String effectId = getEffectIdAsString(entry.getValue());
                if (effectId == null) return null;

                EffectRegister reg = getEffect(effectId);
                if (reg == null || reg.isDisabled()) return null;

                if (!reg.getSlots().contains(slotName.toLowerCase())) return null;
                if (reg.isOnlySneaking() && !player.isSneaking()) return null;
                if (!reg.getDimensions().stream()
                        .map(Utils::normalizeDimensionName)
                        .collect(Collectors.toSet())
                        .contains(Utils.normalizeDimensionName(player.getWorld().getName()))) return null;

                return reg.effects.values(); // Collection<EffectObject>
            })
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(
                    EffectData::getEffectid,
                    e -> e,
                    (e1, e2) -> e1.getPriority() >= e2.getPriority() ? e1 : e2
            ))
            .values().stream()
            .toList();
    }

    public LiteralArgumentBuilder<CommandSourceStack> buildCommands() {
        // TODO Make commands

        commands.then(Commands.literal("geteffectdata").executes(functioncommands::getEffectsDataCommand));
        commands.then(Commands.literal("applyeffect").executes(functioncommands::applyEffectsCommand));

        commands.then(Commands.literal("create_register")
                .then(Commands.argument("id",StringArgumentType.word())
                        .then(Commands.argument("name",StringArgumentType.word())
                                .executes(functioncommands::createEffectRegistery)
                        )
                )
        );

        commands.then(Commands.literal("remove_register")
                .then(Commands.argument("id",StringArgumentType.word()).suggests(
                        (context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(functioncommands::removeEffectRegisteryConfirm)
                        .then(Commands.literal("confirm")
                                .executes(functioncommands::removeEffectRegistery)
                        )
                )
        );

        commands.then(Commands.literal("listactive").executes(functioncommands::listActive));

        commands.then(Commands.literal("getinfo")
                .then(Commands.argument("id",StringArgumentType.word()).suggests(
                        (context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(functioncommands::getInfo)
                )
        );

        commands.then(Commands.literal("listslots")
                .then(Commands.argument("id",StringArgumentType.word()).suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(functioncommands::listSlots)
                )
        );

        commands.then(Commands.literal("add_potion")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("potion", ArgumentTypes.namespacedKey())
                                .suggests((context, builder) -> {
                                    List<String> options = Utils.getAllPotionsKeys();
                                    for (String option : options) {
                                        if (option.startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(option);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("amplifier", IntegerArgumentType.integer(1, 255))
                                        .then(Commands.argument("priority", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                                .executes(functioncommands::addPotionToRegister)
                                        )
                                )
                        )
                )
        );


        commands.then(Commands.literal("add_slot")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("slot", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    List<String> options = List.of("helmet", "chestplate", "leggings", "boots", "mainhand", "offhand");
                                    for (String option : options) {
                                        if (option.startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(option);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(functioncommands::addSlotToRegister)
                        )
                )
        );


        commands.then(Commands.literal("remove_slot")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("slot", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    List<String> options = List.of("helmet", "chestplate", "leggings", "boots", "mainhand", "offhand");
                                    for (String option : options) {
                                        if (option.startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(option);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(functioncommands::removeSlotFromRegister)
                        )
                )
        );

        commands.then(Commands.literal("add_dimension")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("dimension", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    List<String> options = Utils.getDimensions();
                                    for (String option : options) {
                                        if (option.startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(option);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(functioncommands::addDimensionsToRegister)
                        )
                )
        );

        commands.then(Commands.literal("remove_dimension")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("dimension", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    List<String> options = Utils.getDimensions();
                                    for (String option : options) {
                                        if (option.startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(option);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(functioncommands::removeDimensionsFromRegister)
                        )
                )
        );

        commands.then(Commands.literal("remove_potion")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("potion", ArgumentTypes.namespacedKey())
                                .suggests((context, builder) -> {
                                    List<String> options = Utils.getAllPotionsKeys();
                                    for (String option : options) {
                                        if (option.startsWith(builder.getRemainingLowerCase())) {
                                            builder.suggest(option);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(functioncommands::removePotionFromRegister)
                        )
                )
        );

        commands.then(Commands.literal("set_disabled")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("disabled", BoolArgumentType.bool())
                                .executes(functioncommands::setDisabled)
                        )
                )
        );

        commands.then(Commands.literal("set_sneakonly")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("onlysneak", BoolArgumentType.bool())
                                .executes(functioncommands::setSneakonly)
                        )
                )
        );

        commands.then(Commands.literal("attach_effect_to_item")
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            List<String> options = getEffectIds();
                            for (String option : options) {
                                if (option.startsWith(builder.getRemainingLowerCase())) {
                                    builder.suggest(option);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(functioncommands::attachEffectItem)
                )
        );

        return commands;
    }

    public List<EffectData> applyEffects(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        List<EffectData> applylist = this.calculateEffects(player);

        NamespacedKey itemeffectskey = new NamespacedKey(this.main, "masworld_itemapplied_effects");

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();

        String json = pdc.get(itemeffectskey, PersistentDataType.STRING);
        Map<String, Integer> oldEffectsMap = json == null ? new HashMap<>() : gson.fromJson(json, type);

        Map<String, Integer> newEffectsMap = new HashMap<>();
        for (EffectData effect : applylist) {
            newEffectsMap.put(effect.getEffectid(), effect.getAmplifier());
        }

        for (String oldEffectId : oldEffectsMap.keySet()) {
            if (!newEffectsMap.containsKey(oldEffectId)) {
                PotionEffectType typeToRemove = Registry.EFFECT.get(Utils.parseNamespacedKey(oldEffectId));
                if (typeToRemove != null) {
                    player.removePotionEffect(typeToRemove);
                }
            }
        }

        for (EffectData effect : applylist) {
            PotionEffectType typeToApply = Registry.EFFECT.get(Utils.parseNamespacedKey(effect.getEffectid()));
            if (typeToApply != null) {
                player.addPotionEffect(effect.buildPotion());
            }
        }

        pdc.set(itemeffectskey, PersistentDataType.STRING, gson.toJson(newEffectsMap));

        return applylist;
    }

    public String saveToFile(EffectRegister effectRegister) throws IOException {
        if (effectRegister.getPath() == null || effectRegister.getPath().isEmpty()) {
            throw new IllegalArgumentException("EffectRegister.path is not change");
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        String json = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(effectRegister);

        Path filePath = Path.of(effectRegister.getPath());
        Files.writeString(filePath, json);

        this.loadEffects();
        this.reloadPlayerEffects();

        return json;
    }

    public void reloadPlayerEffects() {
        for (Player player : this.main.getServer().getOnlinePlayers()) {
            this.applyEffects(player);
        }
    }
}

/*
.suggests((context, builder) -> {
                    List<String> options = List.of("option1", "option2", "option3");
                    for (String option : options) {
                        if (option.startsWith(builder.getRemainingLowerCase())) {
                            builder.suggest(option);
                        }
                    }
                    return builder.buildFuture();
                })*/