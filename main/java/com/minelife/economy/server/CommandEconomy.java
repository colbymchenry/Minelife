package com.minelife.economy.server;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.permission.ModPermission;
import com.minelife.permission.Player;
import com.minelife.util.NumberConversions;
import com.minelife.util.server.Callback;
import com.minelife.util.server.MLCommand;
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

public class CommandEconomy extends MLCommand {

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
                        "/economy deposit <player> <amount> <wallet:TRUE-FALSE>\n";

        for (String s : commandUsage.split("\n")) {
            sender.addChatMessage(new ChatComponentText(s));
        }

        return commandUsage;
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (!(sender instanceof EntityPlayer)) return true;

        Player player = ModPermission.get(((EntityPlayer) sender).getUniqueID());

        return player.hasPermission("economy");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 2;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
        if (args.length == 0) {
            getCommandUsage(sender);
            return;
        }

        String cmd = args[0].toLowerCase();
        double amount = 0;
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

            amount = Double.parseDouble(args[2]);
            wallet = "true".contains(args[3].toLowerCase()) ? true : false;
        }

        String playerName = args[1];
        UUID playerUUID = UUIDFetcher.get(playerName);

        if(playerUUID == null) {
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
            default: {
                getCommandUsage(sender);
                return;
            }
        }
    }
}
