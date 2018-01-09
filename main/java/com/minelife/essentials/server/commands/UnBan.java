package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.essentials.ModEssentials;
import com.minelife.permission.ModPermission;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class UnBan extends MLCommand {

    @Override
    public String getCommandName() {
        return "unban";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/unban <player>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +"Fetching player's UUID..."));
        UUID playerUUID = UUIDFetcher.get(args[0]);

        if (playerUUID == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found."));
            return;
        }

        if(!Ban.isPlayerBanned(playerUUID)) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player is not banned."));
            return;
        }

        try {
            Ban.UnBanPlayer(playerUUID);
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Player unbanned!"));
        }catch (SQLException e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occurred."));
            e.printStackTrace();
        }

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayerMP) ? true : ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "unban");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }


}
