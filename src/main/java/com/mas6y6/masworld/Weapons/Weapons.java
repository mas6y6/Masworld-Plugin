package com.mas6y6.masworld.Weapons;

import com.mas6y6.masworld.Masworld;

public class Weapons {
    public Masworld main;

    public Weapons(Masworld main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(new Listeners(this), this.main);
    }
}
