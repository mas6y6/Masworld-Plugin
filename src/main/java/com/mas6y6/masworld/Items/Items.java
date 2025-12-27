package com.mas6y6.masworld.Items;

import com.mas6y6.masworld.Masworld;
import com.mas6y6.masworld.Objects.TextSymbols;
import com.mas6y6.masworld.Items.Attributes.DynamiteFuse;
import com.mas6y6.masworld.Items.Attributes.DynamitePower;
import com.mas6y6.masworld.Items.Attributes.SpecialEffect;
import com.mas6y6.masworld.Items.Attributes.WeaponDamage;
import com.mas6y6.masworld.Items.Attributes.*;
import com.mas6y6.masworld.Items.Systems.PumpkinBlindness;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import com.mas6y6.masworld.Items.DrFesh.SuperSpatula;

public class Items {
    public Masworld main;
    public LiteralArgumentBuilder<CommandSourceStack> commands = Commands.literal("attributes");
    public LiteralArgumentBuilder<CommandSourceStack> adminStickCMD = Commands.literal("iwantadminstick");

    public SpecialEffect specialEffect;

    public DynamitePower dynamitePower;
    public DynamiteFuse dynamiteFuse;

    public WeaponDamage weaponDamage;
    public WeaponCooldown weaponCooldown;

    public ShulkerSwordCooldown shulkerSwordCooldown;
    public ShulkerSwordBullet shulkerSwordBullet;
    public ShulkerSwordRange shulkerSwordRange;

    public EvokerBookCooldown evokerBookCooldown;
    public EvokerBookSpacing evokerBookSpacing;
    public EvokerBookRange evokerBookRange;
    public EvokerBookAngle evokerBookAngle;
    public EvokerBookBeamCount evokerBookBeamCount;

    public PumpkinBlindness pumpkinBlindness;
    
    public Items(Masworld main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(new WeaponListeners(this), this.main);
        this.main.getServer().getPluginManager().registerEvents(new EnchantmentListeners(this), this.main);
        this.main.getServer().getPluginManager().registerEvents(new SuperSpatula(this), this.main);

        this.specialEffect = new SpecialEffect(this.main);

        this.dynamitePower = new DynamitePower(this.main);
        this.dynamiteFuse = new DynamiteFuse(this.main);

        this.weaponDamage = new WeaponDamage(this.main);
        this.weaponCooldown = new WeaponCooldown(this.main);

        this.shulkerSwordCooldown = new ShulkerSwordCooldown(this.main);
        this.shulkerSwordBullet = new ShulkerSwordBullet(this.main);
        this.shulkerSwordRange = new ShulkerSwordRange(this.main);

        this.evokerBookCooldown = new EvokerBookCooldown(this.main);
        this.evokerBookSpacing = new EvokerBookSpacing(this.main);
        this.evokerBookRange = new EvokerBookRange(this.main);
        this.evokerBookAngle = new EvokerBookAngle(this.main);
        this.evokerBookBeamCount = new EvokerBookBeamCount(this.main);

        this.pumpkinBlindness = new PumpkinBlindness(this);
    }

    public LiteralArgumentBuilder<CommandSourceStack> buildCommands() {
        commands.then(Commands.literal("special_effect")
                .then(
                        Commands.literal("get").executes(specialEffect::get)
                )
                .then(
                        Commands.literal("change")
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
                        Commands.literal("change")
                                .then(Commands.argument("value", FloatArgumentType.floatArg(1.0f,255.f))
                                        .executes(dynamitePower::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(dynamitePower::reset)
                )
        );

        commands.then(Commands.literal("shulker_sword_cooldown")
                .then(
                        Commands.literal("get").executes(shulkerSwordCooldown::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", LongArgumentType.longArg())
                                        .executes(shulkerSwordCooldown::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(shulkerSwordCooldown::reset)
                )
        );

        commands.then(Commands.literal("shulker_sword_range")
                .then(
                        Commands.literal("get").executes(shulkerSwordRange::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0,255))
                                        .executes(shulkerSwordRange::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(shulkerSwordRange::reset)
                )
        );

        commands.then(Commands.literal("shulker_sword_bullet")
                .then(
                        Commands.literal("get").executes(shulkerSwordBullet::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                        .executes(shulkerSwordBullet::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(shulkerSwordBullet::reset)
                )
        );

        commands.then(Commands.literal("dynamite_fuse")
                .then(
                        Commands.literal("get").executes(dynamiteFuse::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", LongArgumentType.longArg())
                                        .executes(dynamiteFuse::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(dynamiteFuse::reset)
                )
        );

        commands.then(Commands.literal("weapon_damage")
                .then(
                        Commands.literal("get").executes(weaponDamage::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                        .executes(weaponDamage::change)
                                )
                )
                .then(
                        Commands.literal("reset").executes(weaponDamage::reset)
                )
        );

        commands.then(Commands.literal("evoker_book_cooldown")
                .then(
                        Commands.literal("get").executes(evokerBookCooldown::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", LongArgumentType.longArg())
                                        .executes(evokerBookCooldown::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(evokerBookCooldown::reset)
                )
        );

        commands.then(Commands.literal("evoker_book_range")
                .then(
                        Commands.literal("get").executes(evokerBookRange::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                        .executes(evokerBookRange::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(evokerBookRange::reset)
                )
        );

        commands.then(Commands.literal("evoker_book_spacing")
                .then(
                        Commands.literal("get").executes(evokerBookSpacing::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                        .executes(evokerBookSpacing::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(evokerBookSpacing::reset)
                )
        );

        commands.then(Commands.literal("evoker_book_angle")
                .then(
                        Commands.literal("get").executes(evokerBookAngle::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                        .executes(evokerBookAngle::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(evokerBookAngle::reset)
                )
        );

        commands.then(Commands.literal("evoker_book_beamcount")
                .then(
                        Commands.literal("get").executes(evokerBookBeamCount::get)
                )
                .then(
                        Commands.literal("change")
                                .then(Commands.argument("value", IntegerArgumentType.integer(1))
                                        .executes(evokerBookBeamCount::set)
                                )
                )
                .then(
                        Commands.literal("reset").executes(evokerBookBeamCount::reset)
                )
        );

        return commands;
    }

    public LiteralArgumentBuilder<CommandSourceStack> buildAdminStickCMD() {
        adminStickCMD.executes(ctx -> {
            CommandSourceStack sender = ctx.getSource();
            ItemStack adminStick = new GetAdminStick().adminStick();
            if (sender.getExecutor() instanceof Player player) {
                player.getInventory().addItem(adminStick);

                player.sendMessage(TextSymbols.SUCCESS.append(
                        Component.text("BEHOLD: ")
                                .color(NamedTextColor.GREEN)
                                .append(Component.text("admin_stick")
                                        .color(NamedTextColor.BLUE))
                ));
            }
            return 1;
        });
        return adminStickCMD;
    }
}
