package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.Location;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DelWarp implements ICommand {

    @Override
    public String getCommandName() {
        return "delwarp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/delwarp <name>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        EntityPlayerMP Player = (EntityPlayerMP) sender;
        String WarpName = args[0].toLowerCase();

        try {
            Map<String, Location> Warps = Warp.GetWarps(Player.getUniqueID());
            if(!Warps.containsKey(WarpName)) {
                Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Warp not found."));
                return;
            }

            Warp.DeleteWarp(WarpName, Player.getUniqueID());
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Warp deleted!"));
        } catch (SQLException e) {
            e.printStackTrace();
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "delwarp");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if(!(sender instanceof EntityPlayerMP)) return Lists.newArrayList();

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if(args.length == 1) {
            try {
                Map<String, Location> Warps = Warp.GetWarps(Player.getUniqueID());
                List<String> WarpList = Lists.newArrayList();
                WarpList.addAll(Warps.keySet());
                WarpList.add("list");
                return WarpList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
