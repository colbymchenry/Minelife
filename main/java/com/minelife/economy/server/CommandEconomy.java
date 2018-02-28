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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldServer;
import scala.Int;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandEconomy extends MLCommand {

    @Override
    public String getCommandName() {
        return "economy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        String commandUsage =
                "/economy balance <player> <vault|atm>\n" +
                        "/economy set <player> <amount>\n" +
                        "/economy withdraw <player> <amount>\n" +
                        "/economy deposit <player> <amount>\n" +
                        "/economy give <player> <amount>\n" +
                        "/economy take <player> <amount>\n" +
                        "/economy addvault <player> <amount>\n" +
                        "/economy takevault <player> <amount>\n" +
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
        if (args[0].equalsIgnoreCase("get")) return false;
        return index == 1;
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) {
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

            if (cmd.equalsIgnoreCase("balance")) {
                if (args.length != 3) {
                    getCommandUsage(sender);
                    return;
                }
            } else {
                if (args.length != 3) {
                    getCommandUsage(sender);
                    return;
                }

                if (!NumberConversions.isInt(args[2])) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please input an integer for amount."));
                    return;
                }

                amount = Integer.parseInt(args[2]);
            }

            String playerName = args[1];
            UUID playerUUID = UUIDFetcher.get(playerName);

            if (playerUUID == null) {
                sender.addChatMessage(new ChatComponentText("Player not found."));
                return;
            }

            EntityPlayerMP targetPlayer = PlayerHelper.getPlayer(playerUUID);

            switch (cmd.toLowerCase()) {
                case "balance": {
                    scheduledTasks.add(new Runnable() {
                        @Override
                        public void run() {
                            if (args[2].equalsIgnoreCase("vault"))
                                sender.addChatMessage(new ChatComponentText("$" + NumberConversions.formatter.format(MoneyHandler.getBalanceVault(playerUUID))));
                            else if(args[2].equalsIgnoreCase("atm"))
                                sender.addChatMessage(new ChatComponentText("$" + NumberConversions.formatter.format(MoneyHandler.getBalanceATM(playerUUID))));
                            else
                                getCommandUsage(sender);

                        }
                    });
                    return;
                }
                case "set": {
                    MoneyHandler.setATM(playerUUID, amount);
                    sender.addChatMessage(new ChatComponentText("ATM balance updated."));
                    return;
                }
                case "withdraw": {
                    MoneyHandler.withdrawATM(playerUUID, amount);
                    sender.addChatMessage(new ChatComponentText("ATM balance updated."));
                    return;
                }
                case "deposit": {
                    MoneyHandler.depositATM(playerUUID, amount);
                    sender.addChatMessage(new ChatComponentText("ATM balance updated."));
                    return;
                }
                case "take": {
                    if (targetPlayer == null) {
                        MoneyHandler.takeMoneyVault(playerUUID, amount);
                    } else {
                        int couldNotAdd = MoneyHandler.takeMoneyInventory(targetPlayer, amount);
                        targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "$" + amount + " was taken from your account."));
                        if (couldNotAdd > 0) {
                            targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "$" + couldNotAdd + " would not fit in your inventory so it was deposited into your checking account."));
                            MoneyHandler.depositATM(targetPlayer.getUniqueID(), couldNotAdd);
                        }
                    }
                    return;
                }
                case "give": {
                    if (targetPlayer == null) {
                        MoneyHandler.addMoneyVault(playerUUID, amount);
                    } else {
                        int couldNotAdd = MoneyHandler.addMoneyInventory(targetPlayer, amount);
                        targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You were given $" + amount));
                        if (couldNotAdd > 0) {
                            targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "$" + couldNotAdd + " would not fit in your inventory so it was deposited into your checking account."));
                            MoneyHandler.depositATM(targetPlayer.getUniqueID(), couldNotAdd);
                        }
                    }
                    return;
                }
                case "addvault": {
                    if (targetPlayer == null) {
                        MoneyHandler.addMoneyVault(playerUUID, amount);
                    } else {
                        int couldNotAdd = MoneyHandler.addMoneyVault(targetPlayer, amount);
                        targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "$" + amount + " was added to your vault!"));
                        if (couldNotAdd > 0) {
                            targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "$" + couldNotAdd + " would not fit in your vault so it was deposited into your checking account."));
                            MoneyHandler.depositATM(targetPlayer.getUniqueID(), couldNotAdd);
                        }
                    }
                    return;
                }
                case "takevault": {
                    if (targetPlayer == null) {
                        MoneyHandler.takeMoneyVault(playerUUID, amount);
                    } else {
                        int couldNotAdd = MoneyHandler.takeMoneyVault(targetPlayer, amount);
                        targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "$" + amount + " was taken from your vault."));
                        if (couldNotAdd > 0) {
                            targetPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "$" + couldNotAdd + " would not fit in your vault so it was deposited into your checking account."));
                            MoneyHandler.depositATM(targetPlayer.getUniqueID(), couldNotAdd);
                        }
                    }
                    return;
                }
                default: {
                    getCommandUsage(sender);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
