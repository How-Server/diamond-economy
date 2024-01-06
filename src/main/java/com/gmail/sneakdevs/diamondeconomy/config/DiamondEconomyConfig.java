package com.gmail.sneakdevs.diamondeconomy.config;

import com.gmail.sneakdevs.diamondeconomy.DiamondEconomy;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

    public static ItemStack getCurrency(int num) {
        ItemStack paperStack = new ItemStack(Items.PAPER);
        CompoundTag tag = paperStack.getOrCreateTag();

        // Custom properties
        tag.putInt("CustomModelData", 1337031);
        tag.putInt("HideFlags", 1);

        // Display properties (Name and Lore)
        CompoundTag displayTag = new CompoundTag();
        ListTag loreTag = new ListTag();

        displayTag.putString("Name", "[{\"text\":\"\",\"italic\":false},{\"text\":\"How棒棒獎券\",\"color\":\"gold\",\"bold\":true}]");
        loreTag.add(StringTag.valueOf("[{\"text\":\"\",\"italic\":false},{\"text\":\"How服器通用貨幣\"}]"));
        loreTag.add(StringTag.valueOf("[{\"text\":\"\",\"italic\":false},{\"text\":\"參與遊戲和活動即可獲得，\",\"color\":\"green\"}]"));
        loreTag.add(StringTag.valueOf("[{\"text\":\"\",\"italic\":false},{\"text\":\"總之就是什麼都用得到他。\",\"color\":\"green\"}]"));
        loreTag.add(StringTag.valueOf("[{\"text\":\"\",\"italic\":false}]"));
        loreTag.add(StringTag.valueOf("[{\"text\":\"\",\"italic\":false},{\"text\":\"「你How棒，」\",\"color\":\"blue\"}]"));
        loreTag.add(StringTag.valueOf("[{\"text\":\"\",\"italic\":false},{\"text\":\"「獎勵你一根棒棒糖。」\",\"color\":\"blue\"}]"));

        displayTag.put("Lore", loreTag);
        tag.put("display", displayTag);

        // Enchantments
        ListTag enchantmentsTag = new ListTag();
        CompoundTag enchantmentTag = new CompoundTag();
        enchantmentTag.putString("id", "minecraft:protection");
        enchantmentTag.putShort("lvl", (short) 1);
        enchantmentsTag.add(enchantmentTag);
        tag.put("Enchantments", enchantmentsTag);
        return paperStack;
    }

    public static String getCurrencyName(int num) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(DiamondEconomyConfig.getInstance().currencies[num])).getDescription().getString();
    }

    public static int[] getCurrencyValues() {
        return DiamondEconomyConfig.getInstance().currencyValues;
    }

    public static DiamondEconomyConfig getInstance() {
        return AutoConfig.getConfigHolder(DiamondEconomyConfig.class).getConfig();
    }
}