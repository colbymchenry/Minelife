package com.minelife.economy.server;

import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import scala.actors.threadpool.Arrays;

import java.util.List;
import java.util.UUID;

public class CommandEconomy extends MLCommand {

    @Override
    public String getName() {
        return "economy";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("eco", "e");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sendMessage(sender, "/eco withdraw <atm|cash|inventory> <player> <amount>");
        sendMessage(sender, "/eco depositPlayer <atm|cash|inventory> <player> <amount>");
        sendMessage(sender, "/eco set <atm|cash|inventory> <player> <amount>");
        sendMessage(sender, "/eco balance <atm|cash|inventory> <player>");
        return null;
    }

    @Override
    public void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            getUsage(sender);
            return;
        }

        boolean atm = args[1].equalsIgnoreCase("atm");
        boolean cash = args[1].equalsIgnoreCase("cash");
        boolean inventory = args[1].equalsIgnoreCase("inventory");

        if (!atm && !cash && !inventory) {
            getUsage(sender);
            return;
        }

        UUID playerUUID = UUIDFetcher.get(args[2]);

        if (playerUUID == null) {
            sendMessage(sender, "Player not found.");
            return;
        }

        MLCommand.scheduledTasks.add(() -> {
            switch (args[0].toLowerCase()) {
                case "withdraw":
                    if (args.length < 4 || !NumberConversions.isInt(args[3])) {
                        sendMessage(sender, "Amount must be a whole number.");
                        return;
                    }
                    if (atm) ModEconomy.withdrawATM(playerUUID, NumberConversions.toInt(args[3]));
                    else if (cash) ModEconomy.withdrawCashPiles(playerUUID, NumberConversions.toInt(args[3]));
                    else if(inventory) {
                        EntityPlayerMP player = PlayerHelper.getPlayer(playerUUID);
                        if(player == null) {
                            sendMessage(sender, "Player is not online.");
                            return;
                        }
                        int couldNotFit =  ModEconomy.withdrawInventory(player, NumberConversions.toInt(args[3]));
                        System.out.println(couldNotFit);
                        if(couldNotFit > 0) {
                            sendMessage(sender, TextFormatting.RED + "$" + NumberConversions.format(couldNotFit) + TextFormatting.GOLD + " could not fit and was deposited into the player's ATM account.");
                            ModEconomy.depositATM(playerUUID, couldNotFit);
                        }
                    }
                    break;
                case "depositPlayer":
                    if (args.length < 4 || !NumberConversions.isInt(args[3])) {
                        sendMessage(sender, "Amount must be a whole number.");
                        return;
                    }
                    if (atm) ModEconomy.depositATM(playerUUID, NumberConversions.toInt(args[3]));
                    else if (cash) {
                        int couldNotFit = ModEconomy.depositCashPiles(playerUUID, NumberConversions.toInt(args[3]));
                        if(couldNotFit > 0) {
                            sendMessage(sender, TextFormatting.RED + "$" + NumberConversions.format(couldNotFit) + TextFormatting.GOLD + " could not fit and was deposited into the player's ATM account.");
                            ModEconomy.depositATM(playerUUID, couldNotFit);
                        }
                    }
                    else if(inventory) {
                        EntityPlayerMP player = PlayerHelper.getPlayer(playerUUID);
                        if(player == null) {
                            sendMessage(sender, "Player is not online.");
                            return;
                        }
                        int couldNotFit = ModEconomy.depositInventory(player, NumberConversions.toInt(args[3]));
                        if(couldNotFit > 0) {
                            sendMessage(sender, TextFormatting.RED + "$" + NumberConversions.format(couldNotFit) + TextFormatting.GOLD + " could not fit and was deposited into the player's ATM account.");
                            ModEconomy.depositATM(playerUUID, couldNotFit);
                        }
                    }
                    break;
                case "set":
                    if (args.length < 4 || !NumberConversions.isInt(args[3])) {
                        sendMessage(sender, "Amount must be a whole number.");
                        return;
                    }
                    break;
                case "balance":

                    break;
                default:
                    getUsage(sender);
                    break;
            }
        });
    }

    public static void sendMessage(ICommandSender sender, String msg) {
        sender.sendMessage(new TextComponentString(TextFormatting.RED + "[Economy] " + TextFormatting.GOLD + msg));
    }

}
