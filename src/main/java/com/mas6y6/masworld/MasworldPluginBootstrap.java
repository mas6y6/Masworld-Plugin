package com.mas6y6.masworld;


import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import com.mas6y6.masworld.Objects.Utils;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class MasworldPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        LifecycleEventManager<BootstrapContext> manager = context.getLifecycleManager();

        final var MULTIMINE_KEY = EnchantmentKeys.create(Key.key("masworld:multimine"));
        final var MULTIXP_KEY = EnchantmentKeys.create(Key.key("masworld:multixp"));
        final var ITEMMAGNET_KEY = EnchantmentKeys.create(Key.key("masworld:itemmagnet"));
        final var SHOCKER_KEY = EnchantmentKeys.create(Key.key("masworld:shocker"));

        manager.registerEventHandler(
                RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
                    event.registry().register(
                            MULTIMINE_KEY,
                            b -> b.description(Utils.createEnchantmentComponent("\uefe6", TextColor.color(0x9A5CC6),"Multimine"))
                                    .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MINING))
                                    .anvilCost(7)
                                    .maxLevel(3)
                                    .weight(1)
                                    .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 1))
                                    .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 1))
                                    .activeSlots(EquipmentSlotGroup.MAINHAND)
                    );
                })
        );

        manager.registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
           event.registry().register(
                   SHOCKER_KEY,
                   builder -> builder.description(Utils.createEnchantmentComponent("\uefe5", TextColor.color(0x008efa),"Shocker"))
                           .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_SWORD))
                           .anvilCost(7)
                           .maxLevel(3)
                           .weight(1)
                           .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 1))
                           .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(70, 1))
                           .activeSlots(EquipmentSlotGroup.MAINHAND)
           );
        }));

        manager.registerEventHandler(
                RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
                    event.registry().register(
                            MULTIXP_KEY,
                            b -> b.description(Utils.createEnchantmentComponent("\uefe6", TextColor.color(0x47A036),"MultiXP"))
                                    .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MINING))
                                    .anvilCost(10)
                                    .maxLevel(3)
                                    .weight(1)
                                    .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 20))
                                    .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 20))
                                    .activeSlots(EquipmentSlotGroup.MAINHAND)
                    );
                })
        );

        manager.registerEventHandler(
                RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
                    event.registry().register(
                            ITEMMAGNET_KEY,
                            b -> b.description(Utils.createEnchantmentComponent("\uefe4", TextColor.color(0x2CBAA8),"Magnet"))
                                    .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.FOOT_ARMOR))
                                    .anvilCost(10)
                                    .maxLevel(3)
                                    .weight(1)
                                    .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 20))
                                    .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(30, 20))
                                    .activeSlots(EquipmentSlotGroup.MAINHAND)
                    );
                })
        );


        manager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT), event -> {
            final PostFlattenTagRegistrar<@NotNull Enchantment> registrar = event.registrar();

            registrar.addToTag(EnchantmentTagKeys.NON_TREASURE, Set.of(MULTIMINE_KEY));
            registrar.addToTag(EnchantmentTagKeys.NON_TREASURE, Set.of(MULTIXP_KEY));
            registrar.addToTag(EnchantmentTagKeys.NON_TREASURE, Set.of(ITEMMAGNET_KEY));
            registrar.addToTag(EnchantmentTagKeys.NON_TREASURE, Set.of(SHOCKER_KEY));

            registrar.addToTag(EnchantmentTagKeys.DOUBLE_TRADE_PRICE,Set.of(MULTIXP_KEY));
            registrar.addToTag(EnchantmentTagKeys.DOUBLE_TRADE_PRICE,Set.of(ITEMMAGNET_KEY));
            registrar.addToTag(EnchantmentTagKeys.DOUBLE_TRADE_PRICE,Set.of(MULTIMINE_KEY));
            registrar.addToTag(EnchantmentTagKeys.DOUBLE_TRADE_PRICE,Set.of(SHOCKER_KEY));
        });
    }
}