package com.minelife.economy.server;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.permission.ModPermission;
import com.minelife.permission.Player;
import com.minelife.util.server.NameFetcher;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class CommandEconomy implements ICommand {

    @Override
    public String getCommandName()
    {
        return "economy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        String commandUsage = "/economy balance <player> <wallet:TRUE-FALSE>\n" +
                "/economy set <player> <amount> <wallet:TRUE-FALSE>\n" +
                "/economy withdraw <player> <amount> <wallet:TRUE-FALSE>\n" +
                "/economy deposit <player> <amount> <wallet:TRUE-FALSE>\n";

        for (String s : commandUsage.split("\n")) {
            sender.addChatMessage(new ChatComponentText(s));
        }

        return commandUsage;
    }

    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        try {
            if (args.length < 2) {
                getCommandUsage(sender);
                return;
            }

            String player = args[1];
            UUID playerUUID = UUIDFetcher.get(player);
            long amount = 0;
            boolean wallet = false;

            if (playerUUID == null) throw new CustomMessageException("Player not found.");

            String playerName = NameFetcher.get(playerUUID);

            if (playerName == null) throw new CustomMessageException("Player not found.");

            if (args[0].equalsIgnoreCase("balance")) {
                if (args.length < 3) {
                    getCommandUsage(sender);
                    return;
                }
                wallet = args[2].equalsIgnoreCase("t") || !args[2].equalsIgnoreCase("f") && Boolean.parseBoolean(args[2]);
            } else {
                if (args.length < 4) {
                    getCommandUsage(sender);
                    return;
                }
                amount = Long.parseLong(args[2]);
                wallet = args[3].equalsIgnoreCase("t") || !args[3].equalsIgnoreCase("f") && Boolean.parseBoolean(args[3]);
            }

            switch (args[0].toLowerCase()) {
                case "balance": {
                    sender.addChatMessage(new ChatComponentText(ModEconomy.getMessage("Message_Balance")
                            .replace("%b", String.valueOf(ModEconomy.getBalance(playerUUID, wallet)))));
                    break;
                }
                case "set": {
                    ModEconomy.set(playerUUID, amount, wallet);
                    sender.addChatMessage(new ChatComponentText(ModEconomy.getMessage("Message_Set")
                            .replace("%p", playerName)
                            .replace("%b", String.valueOf(amount))
                            .replace("%w", wallet ? "wallet" : "account")));
                    break;
                }
                case "withdraw": {
                    ModEconomy.withdraw(playerUUID, amount, wallet);
                    sender.addChatMessage(new ChatComponentText(ModEconomy.getMessage("Message_Withdraw")
                            .replace("%b", String.valueOf(amount))
                            .replace("%p", playerName)
                            .replace("%w", wallet ? "wallet" : "account")));
                    break;
                }
                case "deposit": {
                    ModEconomy.deposit(playerUUID, amount, wallet);
                    sender.addChatMessage(new ChatComponentText(ModEconomy.getMessage("Message_Deposit")
                            .replace("%b", String.valueOf(amount))
                            .replace("%p", playerName)
                            .replace("%w", wallet ? "wallet" : "account")));
                    break;
                }
                default: {
                    getCommandUsage(sender);
                }
            }
        } catch (Exception e) {
            if (e instanceof CustomMessageException) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + e.getMessage()));
            } else {
                e.printStackTrace();
                Minelife.getLogger().log(Level.SEVERE, "", e);
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if (!(sender instanceof EntityPlayer)) return true;

        Player player = ModPermission.get(((EntityPlayer) sender).getUniqueID());

        return player.hasPermission("economy");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 2;
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }
}
