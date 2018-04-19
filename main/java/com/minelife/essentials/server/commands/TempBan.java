package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TempBan extends MLCommand {

    @Override
    public String getName() {
        return "tempban";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tempban <player> <seconds>";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if(args.length < 2) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        UUID PlayerToBan = UUIDFetcher.get(args[0]);

        if(PlayerToBan == null) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        if(!NumberConversions.isInt(args[1])) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "Please use a whole number for the time in seconds."));
            return;
        }

        int seconds = NumberConversions.toInt(args[1]);

        Ban.BanPlayer(PlayerToBan, seconds);
        Player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Player banned!"));

        if (PlayerHelper.getPlayer(PlayerToBan) != null) {
            PlayerHelper.getPlayer(PlayerToBan).connection.disconnect(new TextComponentString("You are banished from this server."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return !(sender instanceof EntityPlayerMP) || ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tempban");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return isUsernameIndex(args, args.length) ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Lists.newArrayList();
    }

}
