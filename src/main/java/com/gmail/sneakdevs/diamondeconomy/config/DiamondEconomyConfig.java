package com.gmail.sneakdevs.diamondeconomy.config;

import com.gmail.sneakdevs.diamondeconomy.DiamondEconomy;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Collections;
import java.util.List;


@Config(name = DiamondEconomy.MODID)
public class DiamondEconomyConfig implements ConfigData {

    @Comment("List of items used as currency")
    public String[] currencies = {"minecraft:diamond","minecraft:diamond_block"};

    @Comment("Values of each currency in the same order, decimals not allowed (must be in ascending order unless greedyWithdraw is disabled)")
    public int[] currencyValues = {1,9};

    @Comment("Where the diamondeconomy.sqlite file is located (ex: \"C:/Users/example/Desktop/server/world/diamondeconomy.sqlite\")")
    public String fileLocation = null;

    @Comment("Name of the base command (null to disable base command)")
    public String commandName = "how";

    @Comment("Names of the subcommands (null to disable command)")
    public String topCommandName = "top";
    public String balanceCommandName = "balance";
    public String depositCommandName = "deposit";
    public String sendCommandName = "pay";
    public String withdrawCommandName = "withdraw";

    @Comment("Names of the admin subcommands (null to disable command)")
    public String setCommandName = "set";
    public String modifyCommandName = "modify";
    public String visibleCommandName = "visible";

    @Comment("Try to withdraw items using the most high value items possible (ex. diamond blocks then diamonds) \n If disabled withdraw will give player the first item in the list")
    public boolean greedyWithdraw = false;

    @Comment("Money the player starts with when they first join the server")
    public int startingMoney = 0;

    @Comment("How often to add money to each player, in seconds (0 to disable)")
    public int moneyAddTimer = 0;

    @Comment("Amount of money to add each cycle")
    public int moneyAddAmount = 0;

    @Comment("Permission level (1-4) of the op commands in diamond economy. Set to 2 to allow command blocks to use these commands.")
    public int opCommandsPermissionLevel = 4;

    public static ItemStack getCurrency(int num, ServerPlayer player) {
        ItemStack paperStack = new ItemStack(Items.PAPER);
        paperStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(Collections.singletonList(1337039f), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

        paperStack.set(DataComponents.CUSTOM_NAME, Component.literal("How棒棒鈔票").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withItalic(false).withBold(true)));
        List<Component> lore = List.of(
                Component.literal("How服器通用貨幣").withStyle(Style.EMPTY.withItalic(false)),
                Component.literal("參與遊戲和活動即可獲得，").withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false)),
                Component.literal("總之就是什麼都用得到他。").withStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false)),
                Component.literal(""),
                Component.literal("「你How棒，」").withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(false)),
                Component.literal("「獎勵你一根棒棒糖。」").withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE).withItalic(false))
        );
        paperStack.set(DataComponents.LORE, new ItemLore(lore));
        paperStack.getEnchantments().withTooltip(false);
        paperStack.enchant(player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION), 1);
        paperStack.set(DataComponents.ENCHANTMENTS, paperStack.getEnchantments().withTooltip(false));

        return paperStack;
    }

    public static int[] getCurrencyValues() {
        return DiamondEconomyConfig.getInstance().currencyValues;
    }

    public static DiamondEconomyConfig getInstance() {
        return AutoConfig.getConfigHolder(DiamondEconomyConfig.class).getConfig();
    }
}