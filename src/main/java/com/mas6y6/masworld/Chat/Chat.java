package com.mas6y6.masworld.Chat;

import com.mas6y6.masworld.Masworld;

public class Chat {
    Masworld main;

    public Chat(Masworld main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(new Listeners(), this.main);
    }
}