package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.essentials.Location;
import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DelWarp extends CommandBase {

    @Override
    public String getName() {
        return "delwarp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/delwarp <name>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        EntityPlayerMP Player = (EntityPlayerMP) sender;
        String WarpName = args[0].toLowerCase();

        try {
            Map<String, Location> Warps = Warp.GetWarps(Player.getUniqueID());
            if(!Warps.containsKey(WarpName)) {
                Player.sendMessage(new TextComponentString(TextFormatting.RED + "Warp not found."));
                return;
            }

            Warp.DeleteWarp(WarpName, Player.getUniqueID());
            Player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Warp deleted!"));
        } catch (SQLException e) {
            e.printStackTrace();
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "delwarp");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
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

        return null;
    }

}
