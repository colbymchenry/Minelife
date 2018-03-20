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

public class SetWarp extends CommandBase {

    @Override
    public String getName() {
        return "setwarp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/setwarp <name>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
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

        if(PlayerHelper.isOp(Player)) MaxWarps = 500;

        try {
            Map<String, Location> Warps = Warp.GetWarps(Player.getUniqueID());
            if(Warps.size() + 1 > MaxWarps) {
                Player.sendMessage(new TextComponentString(TextFormatting.RED + "You are only allowed to have " + MaxWarps + " warps."));
                return;
            }

            if(Warps.containsKey(args[0].toLowerCase())) {
                Player.sendMessage(new TextComponentString(TextFormatting.RED + "You already have a warp with that name."));
                return;
            }

            Location Location = new Location(Player.getEntityWorld().getWorldInfo().getWorldName(), Player.posX, Player.posY, Player.posZ);
            Location.setYaw(Player.rotationYaw);
            Location.setPitch(Player.rotationPitch);
            Warp.CreateWarp(args[0].toLowerCase(), Location, Player.getUniqueID());
            Player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Warp created!"));
        } catch (SQLException e) {
            e.printStackTrace();
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "setwarp");
    }

}
