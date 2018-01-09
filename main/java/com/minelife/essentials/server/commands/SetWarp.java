package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.essentials.ModEssentials;
import com.minelife.permission.ModPermission;
import com.minelife.util.Location;
import com.minelife.util.NumberConversions;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SetWarp implements ICommand {

    @Override
    public String getCommandName() {
        return "setwarp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setwarp <name>";
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

        int MaxWarps = 0;
        List<String> permissions = ModPermission.getPermissions(Player.getUniqueID());
        for (String node : permissions) {
            if(node.contains("setwarp.")) {
                if(NumberConversions.isInt(node.split("\\.")[1]) && Integer.parseInt(node.split("\\.")[1]) > MaxWarps) {
                    MaxWarps = Integer.parseInt(node.split("\\.")[1]);
                }
            }
        }

        try {
            Map<String, Location> Warps = Warp.GetWarps(Player.getUniqueID());
            if(Warps.size() + 1 > MaxWarps) {
                Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are only allowed to have " + MaxWarps + " warps."));
                return;
            }

            if(Warps.containsKey(args[0].toLowerCase())) {
                Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You already have a warp with that name."));
                return;
            }

            Location Location = new Location(Player.getEntityWorld().getWorldInfo().getWorldName(), Player.posX, Player.posY, Player.posZ);
            Location.setYaw(Player.rotationYaw);
            Location.setPitch(Player.rotationPitch);
            Warp.CreateWarp(args[0].toLowerCase(), Location, Player.getUniqueID());
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Warp created!"));
        } catch (SQLException e) {
            e.printStackTrace();
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "setwarp");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
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
