package com.mas6y6.masworld.Objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class TextSymbols {
    public static final Component WARNING = Component.text("[").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
            .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(" ").color(NamedTextColor.WHITE));


    public static final Component SUCCESS = Component.text("[").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("✓").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
            .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(" ").color(NamedTextColor.WHITE));


    public static final Component ERROR = Component.text("[").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("✗").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
            .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(" ").color(NamedTextColor.WHITE));


    public static final Component INFO = Component.text("[").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("i").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
            .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(" ").color(NamedTextColor.WHITE));

    public static final Component QUESTION = Component.text("[").color(NamedTextColor.DARK_GRAY)
            .append(Component.text("?").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
            .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
            .append(Component.text(" ").color(NamedTextColor.WHITE));

    public static Component warning(String text) {
        return WARNING.append(Component.text(text).color(NamedTextColor.YELLOW));
    }

    public static Component error(String text) {
        return ERROR.append(Component.text(text).color(NamedTextColor.RED));
    }

    public static Component question(String text) {
        return QUESTION.append(Component.text(text).color(NamedTextColor.LIGHT_PURPLE));
    }

    public static Component info(String text) {
        return INFO.append(Component.text(text).color(NamedTextColor.WHITE));
    }

    public static Component success(String text) {
        return SUCCESS.append(Component.text(text).color(NamedTextColor.GREEN));
    }
}