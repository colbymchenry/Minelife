package com.minelife.essentials.server.commands;

import com.minelife.essentials.Location;
import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class SetSpawn extends CommandBase {

    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/setspawn";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        int dimension = Player.dimension;
        double X = Player.posX;
        double Y = Player.posY;
        double Z = Player.posZ;
        float Yaw = Player.rotationYaw;
        float Pitch = Player.rotationPitch;

        Spawn.SetSpawn(new Location(dimension, X, Y, Z, Yaw, Pitch));
        Player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Spawn set!"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "setspawn");
    }

}
