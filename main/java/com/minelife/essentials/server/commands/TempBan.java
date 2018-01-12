package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.UUID;

public class TempBan extends MLCommand {

    @Override
    public String getCommandName() {
        return "tempban";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tempban <player> <seconds>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
        if(args.length < 2) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        UUID PlayerToBan = UUIDFetcher.get(args[0]);

        if(PlayerToBan == null) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found."));
            return;
        }

        if(!NumberConversions.isInt(args[1])) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please use a whole number for the time in seconds."));
            return;
        }

        int seconds = NumberConversions.toInt(args[1]);

        Ban.BanPlayer(PlayerToBan, seconds);
        Player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Player banned!"));

        if (PlayerHelper.getPlayer(PlayerToBan) != null) {
            PlayerHelper.getPlayer(PlayerToBan).playerNetServerHandler.kickPlayerFromServer("You are banished from this server.");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayerMP) ? true : ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tempban");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
