package com.minelife.essentials.server.commands;

import com.minelife.essentials.Location;
import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.SQLException;

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

        try {
            Home.SetHome(Player.getUniqueID(), new Location(Player.getEntityWorld().getWorldInfo().getWorldName(), Player.posX, Player.posY, Player.posZ, Player.rotationYaw, Player.rotationPitch));
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
