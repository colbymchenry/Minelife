package com.minelife.economy.server;

import com.google.common.collect.Lists;
import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.permission.ModPermission;
import com.minelife.permission.Player;
import com.minelife.util.server.Callback;
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

public class CommandEconomy implements ICommand, Callback {

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
        new Thread(new ProcessCommand(this, sender, args)).start();
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

    @Override
    public void callback(Object... objects)
    {
        try {
            ICommandSender sender = (ICommandSender) objects[0];
            String message = (String) objects[1];

            if (objects.length == 6) {
                double amount = (double) objects[2];
                boolean wallet = (boolean) objects[3];
                String command = (String) objects[4];
                UUID player_uuid = (UUID) objects[5];


                if (command.equals("set")) {
                    ModEconomy.set(player_uuid, amount, wallet);
                } else if (command.equals("withdraw")) {
                    ModEconomy.withdraw(player_uuid, amount, wallet);
                } else if (command.equals("deposit")) {
                    ModEconomy.deposit(player_uuid, amount, wallet);
                }
            }

            sender.addChatMessage(new ChatComponentText(message));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    class ProcessCommand implements Runnable {

        private Callback callback;
        private ICommandSender sender;
        private String[] args;

        ProcessCommand(Callback callback, ICommandSender sender, String[] args) {
            this.callback = callback;
            this.sender = sender;
            this.args = args;
        }

        @Override
        public void run()
        {
            try {
                if (args.length < 2) {
                    callback.callback(sender, getCommandUsage(sender));
                    return;
                }

                String player = args[1];
                UUID playerUUID = UUIDFetcher.get(player);
                double amount = 0;
                boolean wallet;

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
                    amount = Double.parseDouble(args[2]);
                    wallet = args[3].equalsIgnoreCase("t") || !args[3].equalsIgnoreCase("f") && Boolean.parseBoolean(args[3]);
                }

                switch (args[0].toLowerCase()) {
                    case "balance": {
                        callback.callback(sender, ModEconomy.getMessage("messages.balance")
                                .replace("%b", String.valueOf(ModEconomy.getBalance(playerUUID, wallet))));
                        break;
                    }
                    case "set": {
                        callback.callback(sender, ModEconomy.getMessage("messages.set")
                                .replace("%p", playerName)
                                .replace("%b", String.valueOf(amount))
                                .replace("%w", wallet ? "wallet" : "account"), amount, wallet, "set", playerUUID);
                        break;
                    }
                    case "withdraw": {
                        callback.callback(sender, ModEconomy.getMessage("messages.withdraw")
                                .replace("%b", String.valueOf(amount))
                                .replace("%p", playerName)
                                .replace("%w", wallet ? "wallet" : "account"), amount, wallet, "withdraw", playerUUID);
                        break;
                    }
                    case "deposit": {
                        callback.callback(sender, ModEconomy.getMessage("messages.deposit")
                                .replace("%b", String.valueOf(amount))
                                .replace("%p", playerName)
                                .replace("%w", wallet ? "wallet" : "account"), amount, wallet, "deposit", playerUUID);
                        break;
                    }
                    default: {
                        callback.callback(sender, getCommandUsage(sender));
                    }
                }
            } catch (Exception e) {
                if (e instanceof CustomMessageException) {
                    callback.callback(sender, EnumChatFormatting.RED + e.getMessage());
                } else {
                    e.printStackTrace();
                    Minelife.getLogger().log(Level.SEVERE, "", e);
                    callback.callback(sender, Minelife.default_error_message);
                }
            }
        }
    }
}
