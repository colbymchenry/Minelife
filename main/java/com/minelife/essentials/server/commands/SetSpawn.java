package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.Location;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class SetSpawn implements ICommand {

    @Override
    public String getCommandName() {
        return "setspawn";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setspawn";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;
        String World = Player.getEntityWorld().getWorldInfo().getWorldName();
        double X = Player.posX;
        double Y = Player.posY;
        double Z = Player.posZ;
        float Yaw = Player.rotationYaw;
        float Pitch = Player.rotationPitch;
        Spawn.SetSpawn(new Location(World, X, Y, Z, Yaw, Pitch));
        Player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Spawn set!"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "setspawn");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
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
