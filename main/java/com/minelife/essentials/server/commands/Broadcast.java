package com.minelife.essentials.server.commands;

import com.minelife.permission.ModPermission;
import com.minelife.util.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class Broadcast extends CommandBase {

    @Override
    public String getName() {
        return "broadcast";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/broadcast <message>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        StringBuilder msg = new StringBuilder();

        for (String arg : args) msg.append(arg);

        for (WorldServer worldServer : server.worlds) {
            for (Object playerEntity : worldServer.playerEntities) {
                ((EntityPlayerMP) playerEntity).sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Server] " + TextFormatting.RESET + StringHelper.ParseFormatting(msg.toString(), '&')));
            }
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return !(sender instanceof EntityPlayer) || ModPermission.hasPermission(((EntityPlayer) sender).getUniqueID(), "broadcast");
    }

}
