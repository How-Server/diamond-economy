package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.UUID;

public class SendCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal(DiamondEconomyConfig.getInstance().sendCommandName)
                .then(
                        Commands.argument("player", GameProfileArgument.gameProfile())
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(e -> {
                                                    Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(e, "player");
                                                    int amount = IntegerArgumentType.getInteger(e, "amount");
                                                    return sendCommand(e, profiles, e.getSource().getPlayerOrException(), amount);
                                                })));
    }

    public static int sendCommand(CommandContext<CommandSourceStack> ctx, Collection<GameProfile> profiles, ServerPlayer sender, int amount) throws CommandSyntaxException {
        if (amount > 1024){
            ctx.getSource().sendSuccess(() -> Component.literal("單次轉帳金額不得超過 $1024"), false);
            return 1;
        }

        DatabaseManager dm = DiamondUtils.getDatabaseManager();

        for (GameProfile profile : profiles) {
            String targetUUID = profile.getId().toString();
            ServerPlayer targetPlayer = ctx.getSource().getServer().getPlayerList().getPlayer(UUID.fromString(targetUUID));

            if (targetPlayer != null || dm.playerExists(targetUUID)) {
                long newValue = dm.getBalanceFromUUID(targetUUID) + amount;

                if (newValue < Integer.MAX_VALUE && dm.changeBalance(sender.getStringUUID(), -amount)) {
                    dm.changeBalance(targetUUID, amount);
                    if (targetPlayer != null && targetPlayer.connection != null) {
                        targetPlayer.displayClientMessage(Component.literal("已收到來自 " + profile.getName() + " 的匯款 $" + amount), true);
                    }
                    ctx.getSource().sendSuccess(() -> Component.literal("已匯款 $" + amount + " 給 " + profile.getName()), true);
                } else {
                    ctx.getSource().sendSuccess(() -> Component.literal("餘額不足"), true);
                }
            } else {
                ctx.getSource().sendSuccess(() -> Component.literal("玩家 " + profile.getName() + " 不在線且沒有進入過伺服器"), true);
            }
        }

        return 1;
    }
}
