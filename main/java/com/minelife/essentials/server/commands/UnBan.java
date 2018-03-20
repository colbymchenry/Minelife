package com.minelife.essentials.server.commands;

import com.minelife.permission.ModPermission;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;
import java.util.UUID;

public class UnBan extends MLCommand {

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/unban <player>";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        sender.sendMessage(new TextComponentString(TextFormatting.GREEN +"Fetching player's UUID..."));
        UUID playerUUID = UUIDFetcher.get(args[0]);

        if (playerUUID == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        if(!Ban.IsPlayerBanned(playerUUID)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player is not banned."));
            return;
        }

        try {
            Ban.UnBanPlayer(playerUUID);
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Player unbanned!"));
        }catch (SQLException e) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred."));
            e.printStackTrace();
        }

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return !(sender instanceof EntityPlayerMP) || ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "unban");
    }

}
