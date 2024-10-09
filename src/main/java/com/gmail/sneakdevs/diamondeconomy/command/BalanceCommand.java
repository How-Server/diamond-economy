package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

public class BalanceCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal(DiamondEconomyConfig.getInstance().balanceCommandName)
                .then(
                        Commands.argument("playerName", StringArgumentType.string())
                                .executes(e -> {
                                    String string = StringArgumentType.getString(e, "playerName");
                                    return balanceCommand(e, string);
                                })
                )
                .then(
                        Commands.argument("player", EntityArgument.player())
                                .executes(e -> {
                                    String player = EntityArgument.getPlayer(e, "player").getName().getString();
                                    return balanceCommand(e, player);
                                })
                )
                .executes(e -> balanceCommand(e, e.getSource().getPlayerOrException().getName().getString()));
    }

    public static int balanceCommand(CommandContext<CommandSourceStack> ctx, String player) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        int bal = dm.getBalanceFromName(player);
        if (dm.isPrivate(player) == 1 && !ctx.getSource().getPlayer().getName().getString().equals(player)) {
            if (ctx.getSource().hasPermission(4)) {
                ctx.getSource().sendSuccess(() -> Component.literal("§e☢ 僅限管理員調閱，此玩家不公開帳戶餘額，請勿外傳"), false);
                ctx.getSource().sendSuccess(() -> Component.literal(player + " 存款為 $" + bal), false);
                return 1;
            }
            ctx.getSource().sendSuccess(() -> Component.literal(player + " 不公開帳戶餘額"), false);
            return 1;
        }
        ctx.getSource().sendSuccess(() -> Component.literal((bal > -1) ? (player + " 存款 $" + bal) : ("查無此人 \"" + player + "\"")), false);
        return 1;
    }
}
