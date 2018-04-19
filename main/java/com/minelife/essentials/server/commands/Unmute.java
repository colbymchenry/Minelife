package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
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

public class Unmute extends MLCommand {

    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/unmute <player>";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return isUsernameIndex(args, args.length) ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Lists.newArrayList();
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if (!(sender instanceof EntityPlayerMP)) return;

        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }
        UUID playerID = UUIDFetcher.get(args[0]);
        if (playerID == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        Mute.removeMutedPlayer((EntityPlayerMP) sender, playerID);
        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "Player unmuted!"));
    }
}