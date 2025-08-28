package com.mas6y6.masworld.Chat;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListeners implements Listener, ChatRenderer {
    @EventHandler
    public void OnChat(AsyncChatEvent event) {
        event.renderer(this);
    }

    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        return Component.text("\"")
            .append(message)
            .append(Component.text("\", says "))
            .append(sourceDisplayName);
    }
}