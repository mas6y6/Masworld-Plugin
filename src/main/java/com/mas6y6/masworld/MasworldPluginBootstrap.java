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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class MasworldPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        LifecycleEventManager<BootstrapContext> manager = context.getLifecycleManager();

        final var MULTIMINE_KEY = EnchantmentKeys.create(Key.key("masworld:multimine"));
        final var MULTIXP_KEY = EnchantmentKeys.create(Key.key("masworld:multixp"));
        final var ITEMMAGNET_KEY = EnchantmentKeys.create(Key.key("masworld:itemmagnet"));

        manager.registerEventHandler(
                RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
                    event.registry().register(
                            MULTIMINE_KEY,
                            b -> b.description(Component.text("Multimine"))
                                    .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MINING))
                                    .anvilCost(7)
                                    .maxLevel(3)
                                    .weight(1)
                                    .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(25, 1))
                                    .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(70, 1))
                                    .activeSlots(EquipmentSlotGroup.MAINHAND)
                    );
                })
        );

        manager.registerEventHandler(
                RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
                    event.registry().register(
                            MULTIXP_KEY,
                            b -> b.description(Component.text("MultiXP"))
                                    .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MINING))
                                    .anvilCost(30)
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
                            b -> b.description(Component.text("Itemmagnet"))
                                    .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.FOOT_ARMOR))
                                    .anvilCost(30)
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
        });
    }
}
