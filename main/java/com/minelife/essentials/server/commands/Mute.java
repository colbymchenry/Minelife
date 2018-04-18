package com.minelife.essentials.server.commands;

import com.google.common.collect.Sets;
import com.minelife.essentials.ModEssentials;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public class Mute extends MLCommand {

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mute <player>";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if(!(sender instanceof EntityPlayerMP)) return;

        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }
        UUID playerID = UUIDFetcher.get(args[0]);
        if(playerID == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        addMutedPlayer((EntityPlayerMP) sender, playerID);
        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Player muted!"));
    }

    public static void addMutedPlayer(EntityPlayerMP player, UUID toMute) {
        try {
            Set<UUID> mutedPlayers = getMutedPlayers(player);
            if(mutedPlayers != null) {
                if(mutedPlayers.contains(toMute)) return;
                StringBuilder builder = new StringBuilder();
                mutedPlayers.forEach(uuid -> builder.append(uuid.toString() + ","));
                builder.append(toMute.toString() + ",");
                ModEssentials.getDB().query("UPDATE mute SET muted='" + builder.toString() + "' WHERE playerID='" + player.getUniqueID().toString() + "'");
            } else {
                ModEssentials.getDB().query("INSERT INTO mute (playerID, muted) VALUES ('" + player.getUniqueID().toString() + "', '" + (toMute.toString() + ",") + "')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeMutedPlayer(EntityPlayerMP player, UUID toMute) {
        try {
            Set<UUID> mutedPlayers = getMutedPlayers(player);
            if(mutedPlayers != null) {
                mutedPlayers.remove(toMute);
                StringBuilder builder = new StringBuilder();
                mutedPlayers.forEach(uuid -> builder.append(uuid.toString() + ","));
                ModEssentials.getDB().query("UPDATE mute SET muted='" + builder.toString() + "' WHERE playerID='" + player.getUniqueID().toString() + "'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Set<UUID> getMutedPlayers(EntityPlayerMP player) {
        Set<UUID> mutedPlayers = Sets.newTreeSet();
        try {
            ResultSet result = ModEssentials.getDB().query("SELECT * FROM mute WHERE playerID='" + player.getUniqueID().toString() + "'");
            if(result.next()) {
                for (String muted : result.getString("muted").split(",")) {
                    if(!muted.isEmpty()) mutedPlayers.add(UUID.fromString(muted));
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mutedPlayers;
    }

}
