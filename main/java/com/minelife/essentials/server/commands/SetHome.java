package com.minelife.essentials.server.commands;

import com.minelife.essentials.Location;
import com.minelife.permission.ModPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SetHome extends CommandBase {

    // TODO: Can't have more than one home, this and warps needs to be cleaned up, also no server warps, only player warps, need to have this swapped, Multiple homes, and warps for everyone
    @Override
    public String getName() {
        return "sethome";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/sethome";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if(args.length == 0) {
            args = new String[]{"default"};
        }

        int MaxHomes = 0;
        List<String> permissions = ModPermission.getPermissions(Player.getUniqueID());
        for (String node : permissions) {
            if(node.contains("sethome.")) {
                if(NumberConversions.isInt(node.split("\\.")[1]) && Integer.parseInt(node.split("\\.")[1]) > MaxHomes) {
                    MaxHomes = Integer.parseInt(node.split("\\.")[1]);
                }
            }
        }

        if(PlayerHelper.isOp(Player)) MaxHomes = 500;

        try {
            Map<String, Location> Homes = Home.GetHomes(Player.getUniqueID());
            if(Homes.size() + 1 > MaxHomes) {
                Player.sendMessage(new TextComponentString(TextFormatting.RED + "You are only allowed to have " + MaxHomes + " homes."));
                return;
            }

            Location Location = new Location(Player.dimension, Player.posX, Player.posY, Player.posZ);
            Location.setYaw(Player.rotationYaw);
            Location.setPitch(Player.rotationPitch);
            Home.SetHome(args[0].toLowerCase(), Location, Player.getUniqueID());
            Player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Home set!"));
        } catch (SQLException e) {
            e.printStackTrace();
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "sethome");
    }

}
