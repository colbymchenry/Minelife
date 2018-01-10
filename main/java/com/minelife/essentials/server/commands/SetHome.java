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

public class SetHome implements ICommand {

    @Override
    public String getCommandName() {
        return "sethome";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/sethome";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        try {
            Home.SetHome(Player.getUniqueID(), new Location(Player.getEntityWorld().getWorldInfo().getWorldName(), Player.posX, Player.posY, Player.posZ, Player.rotationYaw, Player.rotationPitch));
            Player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Home set!"));
        } catch (SQLException e) {
            e.printStackTrace();
            Player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "sethome");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
