package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;

import java.util.Collection;

public class SetCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
        return Commands.literal(DiamondEconomyConfig.getInstance().setCommandName)
                .requires((permission) -> permission.hasPermission(DiamondEconomyConfig.getInstance().opCommandsPermissionLevel))
                .then(
                        Commands.argument("players", GameProfileArgument.gameProfile())
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return setCommand(e, GameProfileArgument.getGameProfiles(e, "players"), amount);
                                                }))
                )
                .then(
                        Commands.argument("amount", IntegerArgumentType.integer(0))
                                .then(
                                        Commands.argument("shouldModifyAll", BoolArgumentType.bool())
                                                .executes(e -> {
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    boolean shouldModifyAll = BoolArgumentType.getBool(e, "shouldModifyAll");
                                                    return setCommand(e, amount, shouldModifyAll);
                                                })
                                )
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return setCommand(e, amount, false);
                                })
                );
    }

    public static int setCommand(CommandContext<CommandSourceStack> ctx, Collection<GameProfile> profiles, int amount) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        profiles.forEach(profile -> dm.setBalance(profile.getId().toString(), amount));
        ctx.getSource().sendSuccess(() -> Component.literal("更新帳戶存款 " + profiles.size() + " 位 " + amount), true);
        return profiles.size();
    }

    public static int setCommand(CommandContext<CommandSourceStack> ctx, int amount, boolean shouldModifyAll) throws CommandSyntaxException {
        if (shouldModifyAll) {
            DiamondUtils.getDatabaseManager().setAllBalance(amount);
            ctx.getSource().sendSuccess(() -> Component.literal("所有帳戶存款設定為 " + amount), true);
        } else {
            Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(ctx, "players");
            profiles.forEach(profile -> DiamondUtils.getDatabaseManager().setBalance(profile.getId().toString(), amount));
            ctx.getSource().sendSuccess(() -> Component.literal("更新帳戶存款 " + profiles.size() + " 位 " + amount), true);
        }
        return 1;
    }
}
