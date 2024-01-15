package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class WithdrawCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal(DiamondEconomyConfig.getInstance().withdrawCommandName)
                .then(
                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(e -> {
                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                    return withdrawCommand(e, amount);
                                })
                );
    }

    public static int withdrawCommand(CommandContext<CommandSourceStack> ctx, int amount) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        if(arePlayersNearby(player)){
            ctx.getSource().sendSuccess(() -> Component.literal("您附近有其他玩家，無法提款"), false);
            return 1;
        }
        if (amount > 1024){
            ctx.getSource().sendSuccess(() -> Component.literal("單次提領金額不得超過 $1024"), false);
            return 1;
        }
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        if (dm.changeBalance(player.getStringUUID(), -amount)) {
            ctx.getSource().sendSuccess(() -> Component.literal("已領出 $" + (amount - DiamondUtils.dropItem(amount, player))), true);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("您的存款少於 $" + amount), true);
        }
        return 1;
    }
    private static boolean arePlayersNearby(ServerPlayer player) {
        for(ServerPlayer player1 : player.getServer().getPlayerList().getPlayers()){
            if (player.distanceTo(player1) <= 3 && !player.equals(player1)){
                System.out.println(player.distanceTo(player1));
                return true;
            }
        }return false;
    }

}
