package com.minelife.jobs.job.bountyhunter;

import com.google.common.collect.Maps;
import com.minelife.economy.ModEconomy;
import com.minelife.jobs.ModJobs;
import com.minelife.util.NumberConversions;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.NameFetcher;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class CommandBounty extends MLCommand {

    @Override
    public String getName() {
        return "bounty";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/bounty place <player> <amount>\n/bounty remove <player>\n/bounty <player>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if(args.length < 2) {
            if(args.length == 1) {
                UUID target = UUIDFetcher.get(args[0]);
                if(target == null) {
                    if(target == null) {
                        sendMessage(sender, TextFormatting.RED + "Player not found.");
                        return;
                    }
                }

                sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "------[Bounties: " + TextFormatting.RED + NameFetcher.get(target) + TextFormatting.GOLD + "]------"));
                getBounties(target).forEach((placerName, amount) -> {
                    sender.sendMessage(new TextComponentString(TextFormatting.GOLD + placerName + " - " + TextFormatting.RED + "$" + NumberConversions.format(amount)));
                });
                return;
            }
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        UUID target = UUIDFetcher.get(args[1]);

        if(target == null) {
            sendMessage(sender, TextFormatting.RED + "Player not found.");
            return;
        }

        if(args[0].equalsIgnoreCase("place")) {
            if(args.length < 3) {
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                return;
            }
            if(!NumberConversions.isInt(args[2])) {
                sendMessage(sender, TextFormatting.RED + "Amount must be a whole number.");
                return;
            }

            int amount = NumberConversions.toInt(args[2]);
            if(ModEconomy.getBalanceInventory((EntityPlayer) sender) < amount) {
                sendMessage(sender, TextFormatting.RED + "Insufficient funds in inventory.");
                return;
            }

            int didNotFit = ModEconomy.withdrawInventory((EntityPlayerMP) sender, amount);
            if(didNotFit > 0) {
                ModEconomy.depositATM(((EntityPlayerMP) sender).getUniqueID(), didNotFit, false);
                sendMessage(sender, TextFormatting.RED + "$" + NumberConversions.format(didNotFit) + TextFormatting.GOLD + " did not fit in your inventory and was deposited into your ATM.");
            }

            createBounty((EntityPlayerMP) sender, target, NumberConversions.toInt(args[2]));
        } else if (args[0].equalsIgnoreCase("remove")) {
            if(BountyHunterListener.playerDeaths.containsKey(target) && BountyHunterListener.playerDeaths.get(target) > System.currentTimeMillis()) {
                sendMessage(sender, "Player died recently, you cannot remove the bounty.");
                return;
            }
            removeBounty((EntityPlayerMP) sender, target);
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }

    public static void sendMessage(ICommandSender sender, String msg) {
        sender.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "[Bounty] " + TextFormatting.GOLD + msg));
    }

    public static void createBounty(EntityPlayerMP placer, UUID target, int amount) {
        try {
            ResultSet result = ModJobs.getDatabase().query("SELECT * FROM bounties WHERE placer='" + placer.getUniqueID().toString() + "' AND target='" + target.toString() + "'");
            if(result.next()) {
                sendMessage(placer, TextFormatting.RED + "You already have a bounty out for that player.");
                return;
            }
            ModJobs.getDatabase().query("INSERT INTO bounties (placer, target, amount) VALUES ('" + placer.getUniqueID().toString() + "', '" + target.toString() + "', '" + amount + "')");
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessage(placer, TextFormatting.RED + "An error occurred. Please notify an admin.");
        }

        sendMessage(placer, "Bounty placed for " + TextFormatting.RED + "$" + NumberConversions.format(amount) + TextFormatting.GOLD + "!");
    }

    public static void removeBounty(EntityPlayerMP placer, UUID target) {
        try {
            ResultSet result = ModJobs.getDatabase().query("SELECT * FROM bounties WHERE placer='" + placer.getUniqueID().toString() + "' AND target='" + target.toString() + "'");
            if(!result.next()) {
                sendMessage(placer, TextFormatting.RED + "Bounty for that player not found by you.");
                return;
            }

            int amount = result.getInt("amount");
            ModEconomy.depositATM(placer.getUniqueID(), amount, true);

            ModJobs.getDatabase().query("DELETE FROM bounties WHERE placer='" + placer.getUniqueID().toString() + "' AND target='" + target.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessage(placer, TextFormatting.RED + "An error occurred. Please notify an admin.");
        }

        sendMessage(placer, "Bounty removed.");
    }

    public static Map<String, Integer> getBounties(UUID target) {
        Map<String, Integer> bounties = Maps.newHashMap();
        try {
            ResultSet result = ModJobs.getDatabase().query("SELECT * FROM bounties WHERE target='" + target.toString() + "'");
            while(result.next()) bounties.put(NameFetcher.get(UUID.fromString(result.getString("placer"))), result.getInt("amount"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bounties;
    }

    public static boolean removeBounty(UUID target) {
        try {
            ModJobs.getDatabase().query("DELETE FROM bounties WHERE target='" + target.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
