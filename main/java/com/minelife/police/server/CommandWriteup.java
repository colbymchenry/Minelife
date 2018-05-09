package com.minelife.police.server;

import com.minelife.Minelife;
import com.minelife.police.ModPolice;
import com.minelife.police.network.PacketOpenWriteupGUI;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.UUID;

public class CommandWriteup extends MLCommand {

    @Override
    public String getName() {
        return "writeup";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/writeup <player>";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if(args.length != 1) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "/writeup <player>"));
            return;
        }

        UUID playerID = UUIDFetcher.get(args[0]);

        if(playerID == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        Minelife.getNetwork().sendTo(new PacketOpenWriteupGUI(playerID), (EntityPlayerMP) sender);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        if(!(sender instanceof EntityPlayer)) return false;
        EntityPlayer player = (EntityPlayer) sender;
        return ModPolice.isCop(player.getUniqueID());
    }
}
