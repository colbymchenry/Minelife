package com.minelife.essentials.server.commands;

import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

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