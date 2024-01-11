package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DepositCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal(DiamondEconomyConfig.getInstance().depositCommandName)
                .executes(DepositCommand::depositCommand);
    }

    public static int depositCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        int currencyCount = 0;
        for (int i = DiamondEconomyConfig.getCurrencyValues().length - 1; i >= 0; i--) {
            for (int j = 0; j < player.getInventory().getContainerSize(); j++) {
                if (isHowToken(player.getInventory().getItem(j))) {
                    currencyCount += player.getInventory().getItem(j).getCount();
                    player.getInventory().setItem(j, new ItemStack(Items.AIR));
                }
            }
        }
        if (dm.changeBalance(player.getStringUUID(), currencyCount)) {
            String output = "已存入 $" + currencyCount + " 到How銀行";
            ctx.getSource().sendSuccess(() -> Component.literal(output), true);
        } else {
            DiamondUtils.dropItem(currencyCount, player);
        }
        return 1;
    }
    private static boolean isHowToken(ItemStack itemStack){
        if(!itemStack.getItem().equals(Items.PAPER)) return false;
        if(itemStack.getTag() != null){
            return itemStack.getTag().getInt("CustomModelData") == 1337031;
        }
        return false;
    }

}
