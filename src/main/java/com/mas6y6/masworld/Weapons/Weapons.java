package com.mas6y6.masworld.Weapons;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Weapons.Attributes.DynamiteFuse;
import com.mas6y6.masworld.Weapons.Attributes.DynamitePower;
import com.mas6y6.masworld.Weapons.Attributes.SpecialEffect;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class Weapons {
    public Masworld main;
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("attributes");

    public SpecialEffect specialEffect;
    public DynamitePower dynamitePower;
    public DynamiteFuse dynamiteFuse;

    public Weapons(Masworld main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(new Listeners(this), this.main);

        this.specialEffect = new SpecialEffect(this.main);
        this.dynamitePower = new DynamitePower(this.main);
        this.dynamiteFuse = new DynamiteFuse(this.main);
    }

    public LiteralArgumentBuilder<CommandSourceStack> buildCommands() {
        commands.then(Commands.literal("special_effect")
                .then(
                        Commands.literal("get").executes(specialEffect::get)
                )
                .then(
                        Commands.literal("set")
                                .then(Commands.argument("value",StringArgumentType.word())
                                        .executes(specialEffect::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(specialEffect::reset)
                )
        );

        commands.then(Commands.literal("dynamite_power")
                .then(
                        Commands.literal("get").executes(dynamitePower::get)
                )
                .then(
                        Commands.literal("set")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(1.0f,100.f))
                                        .executes(dynamitePower::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(dynamitePower::reset)
                )
        );

        commands.then(Commands.literal("dynamite_fuse")
                .then(
                        Commands.literal("get").executes(dynamiteFuse::get)
                )
                .then(
                        Commands.literal("set")
                                .then(Commands.argument("value", LongArgumentType.longArg())
                                        .executes(dynamiteFuse::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(dynamiteFuse::reset)
                )
        );

        return commands;
    }
}
