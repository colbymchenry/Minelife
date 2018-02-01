package com.minelife.economy.server;

import com.google.common.collect.Lists;
import com.minelife.MLItems;
import com.minelife.economy.ItemMoney;
import com.minelife.economy.ItemWallet;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.MoneyHandler;
import com.minelife.permission.ModPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import scala.Int;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandEconomy implements ICommand {

    @Override
    public String getCommandName() {
        return "economy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        String commandUsage =
                "/economy balance <player> <wallet:TRUE-FALSE>\n" +
                        "/economy set <player> <amount> <wallet:TRUE-FALSE>\n" +
                        "/economy withdraw <player> <amount> <wallet:TRUE-FALSE>\n" +
                        "/economy deposit <player> <amount> <wallet:TRUE-FALSE>\n" +
                        "/economy get <amount>\n";

        for (String s : commandUsage.split("\n")) {
            sender.addChatMessage(new ChatComponentText(s));
        }

        return commandUsage;
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("eco", "money");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (!(sender instanceof EntityPlayer)) return true;
        return ModPermission.hasPermission(((EntityPlayer) sender).getUniqueID(), "economy");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            if (args.length == 0) {
                getCommandUsage(sender);
                return;
            }

            if (args[0].equalsIgnoreCase("get")) {
                if (!(sender instanceof EntityPlayerMP)) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be a player to do this."));
                    return;
                }

                if (args.length < 2) {
                    getCommandUsage(sender);
                    return;
                }

                if (!NumberConversions.isInt(args[1])) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Amount must be an integer."));
                    return;
                }

                List<ItemStack> stacks = ItemMoney.getDrops(NumberConversions.toInt(args[1]));

                if (stacks == null) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Max amount at a time: $3,456,000"));
                    return;
                }

                if (stacks.size() > 63) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many stacks."));
                    return;
                }

                ItemStack bagOCash = new ItemStack(MLItems.bagOCash);
                ItemWallet.setHoldings(bagOCash, stacks);
                EntityItem entity_item = ((EntityPlayerMP) sender).dropPlayerItemWithRandomChoice(bagOCash, false);
                if (entity_item != null) entity_item.delayBeforeCanPickup = 0;
                return;
            }

            String cmd = args[0].toLowerCase();
            int amount = 0;
            boolean wallet;

            if (cmd.equalsIgnoreCase("balance")) {
                if (args.length != 3) {
                    getCommandUsage(sender);
                    return;
                }

                wallet = "true".contains(args[2].toLowerCase()) ? true : false;
            } else {
                if (args.length != 4) {
                    getCommandUsage(sender);
                    return;
                }

                if (!NumberConversions.isInt(args[2])) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please input an integer for amount."));
                    return;
                }

                amount = Integer.parseInt(args[2]);
                wallet = "true".contains(args[3].toLowerCase()) ? true : false;
            }

            String playerName = args[1];
            UUID playerUUID = UUIDFetcher.get(playerName);

            if (playerUUID == null) {
                sender.addChatMessage(new ChatComponentText("Player not found."));
                return;
            }

            switch (cmd.toLowerCase()) {
                case "balance": {
                    sender.addChatMessage(new ChatComponentText("$" + NumberConversions.formatter.format(ModEconomy.getBalance(playerUUID, wallet))));
                    return;
                }
                case "set": {
                    ModEconomy.set(playerUUID, amount, wallet);
                    sender.addChatMessage(new ChatComponentText("Balance updated."));
                    return;
                }
                case "withdraw": {
                    ModEconomy.withdraw(playerUUID, amount, wallet);
                    sender.addChatMessage(new ChatComponentText("Balance updated."));
                    return;
                }
                case "deposit": {
                    ModEconomy.deposit(playerUUID, amount, wallet);
                    sender.addChatMessage(new ChatComponentText("Balance updated."));
                    return;
                }
                case "take": {
                    MoneyHandler.takeMoneyInventory(PlayerHelper.getPlayer(playerUUID), amount);
                }
                default: {
                    getCommandUsage(sender);
                    return;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
