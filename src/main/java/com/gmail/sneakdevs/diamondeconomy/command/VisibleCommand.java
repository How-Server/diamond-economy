package com.gmail.sneakdevs.diamondeconomy.command;

import com.gmail.sneakdevs.diamondeconomy.DiamondUtils;
import com.gmail.sneakdevs.diamondeconomy.config.DiamondEconomyConfig;
import com.gmail.sneakdevs.diamondeconomy.sql.DatabaseManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class VisibleCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> buildCommand(){
        return Commands.literal(DiamondEconomyConfig.getInstance().visibleCommandName)
                    .executes(e -> visibleCommand(e));
    }

    public static int visibleCommand(CommandContext<CommandSourceStack> ctx) {
        DatabaseManager dm = DiamondUtils.getDatabaseManager();
        if (dm.isPrivate(ctx.getSource().getPlayer().getName().getString()) == 1) {
            ctx.getSource().sendSuccess(() -> Component.literal("§a 已將您的餘額設為公開"), false);
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal("§c 已將您的餘額設為不公開"), false);
        }
        dm.setVisibility(ctx.getSource().getPlayer().getName().getString());
        return 1;
    }
}
