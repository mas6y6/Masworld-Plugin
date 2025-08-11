package com.mas6y6.masworld.ItemEffects.Objects;

import com.mas6y6.masworld.ItemEffects.ItemEffects;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mas6y6.masworld.Objects.Utils;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionCommands {
    public ItemEffects itemeffects;

    public FunctionCommands(ItemEffects itemEffects) {
        this.itemeffects = itemEffects;
    }

    public int getEffectsDataCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!")));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.debug"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.WHITE)));
                return 0;
            }

            List<EffectData> list = itemeffects.calculateEffects(player);

            if (!(list.size() == 0)) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Effects To Apply:").color(NamedTextColor.WHITE)));
            }

            for (EffectData effect : list) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- ").append(Component.text(effect.getEffectid() + " | " + effect.getPriority()).color(NamedTextColor.WHITE))));
            }

            if (list.size() == 0) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("No effects")));
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    public int applyEffectsCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.debug"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        List<EffectData> applylist = itemeffects.applyEffects(player);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Applied Effects!").color(NamedTextColor.GREEN)));

        for (EffectData effect : applylist) {
            source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- ").append(Component.text(effect.getEffectid() + " | " + effect.getPriority()).color(NamedTextColor.WHITE))));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int createEffectRegistery(CommandContext<CommandSourceStack> context) {
        String id = context.getArgument("id",String.class);
        String name = context.getArgument("name",String.class);

        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }
        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Creating EffectRegister...").color(NamedTextColor.WHITE)));

        EffectRegister effectRegister = new EffectRegister();
        effectRegister.cfgver = this.itemeffects.cfgver;
        Path dir = Paths.get(this.itemeffects.dir.toURI());
        effectRegister.path = String.valueOf(dir.resolve(effectRegister.name).toUri());
        effectRegister.name = name;
        effectRegister.id = id;

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully created and registered \""+effectRegister.name+"\" at \""+effectRegister.id+"\"").color(NamedTextColor.WHITE)));
        this.itemeffects.registerEffect(effectRegister);

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return Command.SINGLE_SUCCESS;
    }

    public int listActive(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (this.itemeffects.effects.isEmpty()) {
            source.getSender().sendMessage(TextSymbols.WARNING.append(Component.text("There are no EffectRegisters active!").color(NamedTextColor.RED)));
        } else {
            ArrayList<String> active = new ArrayList<>();
            ArrayList<String> disabled = new ArrayList<>();

            for (Map.Entry<String, EffectRegister> entry : this.itemeffects.effects.entrySet()) {
                if (entry.getValue().disabled) {
                    disabled.add(entry.getKey());
                } else {
                    active.add(entry.getKey());
                }
            }

            source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("All EffectRegisters").color(NamedTextColor.WHITE)));
            for (String id : active) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- "+id).color(NamedTextColor.GREEN)));
            }
            for (String id : disabled) {
                source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- "+id).color(NamedTextColor.RED)));
            }
        }

        return 0;
    }

    public int listSlots(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id",String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        EffectRegister effectregister = this.itemeffects.getEffect(id);

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Effective Slots for \""+effectregister.name+"\"").color(NamedTextColor.WHITE)));

        for (String slot : effectregister.getSlots()) {
            source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("- "+slot.toLowerCase()).color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int addPotionToRegister(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id",String.class);
        String potion = context.getArgument("potion",String.class);
        Integer amplifier = context.getArgument("amplifier",Integer.class);
        Integer priority = context.getArgument("priority",Integer.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (!(amplifier >= 1 && amplifier <= 255)) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("Argument \"amplifier\" most be within the range of 1 and 255").color(NamedTextColor.RED)));
            return 0;
        }

        NamespacedKey namekey = Utils.parseNamespacedKey(potion);
        if (Registry.EFFECT.get(namekey) == null) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("PotionTypeEffect \""+potion+"\" doesn't exist!").color(NamedTextColor.RED)));
            return 0;
        }

        if (this.itemeffects.getEffect(id) == null) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("EffectRegistry \""+id+"\" doesn't exist!").color(NamedTextColor.RED)));
            return 0;
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();
        EffectData effectData = new EffectData();
        PotionEffectType potionEffectType = Registry.EFFECT.get(namekey);

        effectData.amplifier = amplifier;
        effectData.priority = priority;
        effectData.effectid = potion;
        effectData.effecttype = potionEffectType;

        effectRegister.effects.put(potion,effectData);

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully added "+potion+" to \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int addSlotToRegister(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id",String.class);
        String slot = context.getArgument("slot",String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (!(List.of("helmet","chestplate","leggings","boots","mainhand","offhand").contains(slot))) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("\"slot\" must be the following [helmet,chestplate,leggings,boots,mainhand,offhand]").color(NamedTextColor.RED)));
            return 0;
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();
        effectRegister.slots.add(slot);

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully added "+slot+" to \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int addDimensionsToRegister(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id",String.class);
        String dimension = context.getArgument("dimension",String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (!(Utils.getDimensions().contains(dimension))) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("\"dimension\" must be the following "+ Utils.getDimensions()).color(NamedTextColor.RED)));
            return 0;
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();
        effectRegister.dimensions.add(dimension);

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully added "+dimension+" to \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int removeDimensionsFromRegister(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id",String.class);
        String dimension = context.getArgument("dimension",String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (!(Utils.getDimensions().contains(dimension))) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("\"dimension\" must be the following "+ Utils.getDimensions()).color(NamedTextColor.RED)));
            return 0;
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();
        effectRegister.dimensions.remove(dimension);

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully removed "+dimension+" from \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int removeSlotFromRegister(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id",String.class);
        String slot = context.getArgument("slot",String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (!(List.of("helmet","chestplate","leggings","boots","mainhand","offhand").contains(slot))) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("\"slot\" must be the following [helmet,chestplate,leggings,boots,mainhand,offhand]").color(NamedTextColor.RED)));
            return 0;
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();
        effectRegister.slots.remove(slot);

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully removed "+slot+" from \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int removePotionFromRegister(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id",String.class);
        String potion = context.getArgument("potion",String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (Registry.EFFECT.get(Utils.parseNamespacedKey(potion)) == null) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("PotionTypeEffect \""+potion+"\" doesn't exist!").color(NamedTextColor.RED)));
            return 0;
        }

        if (this.itemeffects.getEffect(id) == null) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("EffectRegistry \""+id+"\" doesn't exist!").color(NamedTextColor.RED)));
            return 0;
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();

        effectRegister.effects.remove(potion);

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully removed "+potion+" from \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int setDisabled(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id", String.class);
        Boolean disabled = context.getArgument("disabled", Boolean.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();
        effectRegister.disabled = disabled;

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully disabled \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int setSneakonly(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id", String.class);
        Boolean onlysneak = context.getArgument("onlysneak", Boolean.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id).copy();
        effectRegister.onlysneaking = onlysneak;

        this.itemeffects.modifyEffect(id,effectRegister);

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully disabled \""+id+"\"").color(NamedTextColor.WHITE)));

        source.getSender().sendMessage(TextSymbols.INFO.append(Component.text("Saving EffectRegister to files.").color(NamedTextColor.YELLOW)));
        try {
            this.itemeffects.saveToFile(effectRegister);
        } catch (Exception e) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Failed to save EffectRegister!").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("If plugin reloads this EffectRegister will be lost").color(NamedTextColor.RED)));
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("Please check console for errors!").color(NamedTextColor.RED)));
            e.printStackTrace();
        } finally {
            source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully saved \""+effectRegister.name+"\" at \""+effectRegister.path+"\"").color(NamedTextColor.WHITE)));
        }

        return 0;
    }

    public int removeEffectRegisteryConfirm(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id", String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        if (this.itemeffects.getEffect(id) == null) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("EffectRegister \""+id+"\" doesnt exist").color(NamedTextColor.RED)));
            return 0;
        }

        source.getSender().sendMessage(TextSymbols.WARNING.append(Component.text("Are you sure that you want to delete EffectRegister at \""+id+"\"?").color(NamedTextColor.RED)));
        source.getSender().sendMessage(TextSymbols.WARNING.append(
                Component.text("To confirm please ").append(
                            Component.text("click here")
                                    .color(NamedTextColor.RED)
                                    .hoverEvent(HoverEvent.showText(Component.text("Click me to paste command").color(NamedTextColor.RED)))
                                    .clickEvent(ClickEvent.runCommand("/masworld itemeffects remove_register \" "+id+" \" confirm"))
                        ).append(Component.text(" to run this command"))
                )
        );

        return 0;
    }

    public int removeEffectRegistery(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id", String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id);

        if (effectRegister == null) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("EffectRegister \""+id+"\" doesnt exist").color(NamedTextColor.RED)));
            return 0;
        }

        source.getSender().sendMessage(TextSymbols.WARNING.append(Component.text("Removing \""+id+"\" from the EffectRegistry...").color(NamedTextColor.YELLOW)));
        this.itemeffects.effects.remove(effectRegister);
        source.getSender().sendMessage(TextSymbols.WARNING.append(Component.text("Reloading all player effects...").color(NamedTextColor.YELLOW)));
        this.itemeffects.reloadPlayerEffects();

        File file = new File(effectRegister.path);
        if (file.delete()) {
            source.getSender().sendMessage(TextSymbols.WARNING.append(Component.text("Removed file").color(NamedTextColor.YELLOW)));
        } else {
            source.getSender().sendMessage(TextSymbols.WARNING.append(Component.text("Failed to remove file").color(NamedTextColor.YELLOW)));
        }

        source.getSender().sendMessage(TextSymbols.SUCCESS.append(Component.text("Successfully removed \""+id+"\" from the EffectRegistry").color(NamedTextColor.WHITE)));

        return 0;
    }

    public int getInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String id = context.getArgument("id", String.class);

        if (!(source.getSender() instanceof Player player)) {
            source.getSender().sendMessage(TextSymbols.ERROR.append(Component.text("You must be a Player!").color(NamedTextColor.WHITE)));
            return 0;
        } else {
            if (!(player.hasPermission("masworld.itemeffects.editor"))) {
                player.sendMessage(TextSymbols.ERROR.append(Component.text("You don't have the permission to run this command!").color(NamedTextColor.RED)));
                return 0;
            }
        }

        EffectRegister effectRegister = this.itemeffects.getEffect(id);
        if (effectRegister == null) {
            player.sendMessage(TextSymbols.ERROR.append(Component.text("EffectRegister \""+id+"\" doesnt exist").color(NamedTextColor.RED)));
            return 0;
        }

        player.sendMessage(TextSymbols.SUCCESS.append(Component.text("EffectRegister at \""+effectRegister.id+"\"").color(NamedTextColor.WHITE)));
        player.sendMessage(Component.text(effectRegister.name).color(NamedTextColor.GREEN));
        player.sendMessage(Component.text("Is disabled: "+effectRegister.disabled).color(NamedTextColor.WHITE));
        player.sendMessage(Component.text("Is sneak only: "+effectRegister.disabled).color(NamedTextColor.WHITE));
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Applies in the following dimensions:").color(NamedTextColor.WHITE));
        for (String dimension : effectRegister.dimensions) {
            player.sendMessage(Component.text("- "+dimension).color(NamedTextColor.LIGHT_PURPLE));
        }
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Applies in the following slots:"+effectRegister.disabled).color(NamedTextColor.WHITE));
        for (String slot : effectRegister.slots) {
            player.sendMessage(Component.text("- "+slot).color(NamedTextColor.GRAY));
        }
        player.sendMessage(Component.empty());
        player.sendMessage(Component.text("Applies the following effects:"+effectRegister.disabled).color(NamedTextColor.WHITE));
        for (Map.Entry<String,EffectData> entry : effectRegister.getEffects().entrySet()) {
            String key = entry.getKey();
            EffectData effect = entry.getValue();
            player.sendMessage(Component.text(key).color(NamedTextColor.GREEN));
            player.sendMessage(Component.text(" Amplifier: "+effect.amplifier).color(NamedTextColor.LIGHT_PURPLE));
            player.sendMessage(Component.text(" Priority: "+effect.priority).color(NamedTextColor.YELLOW));
        }

        return 0;
    }
}
