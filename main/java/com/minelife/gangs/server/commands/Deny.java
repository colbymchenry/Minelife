package com.minelife.gangs.server.commands;

import com.minelife.gangs.network.PacketInviteToGang;
import com.minelife.ICommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Deny implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayerMP playerSender = (EntityPlayerMP) sender;

        if(!PacketInviteToGang.gangInvites.containsKey(playerSender.getUniqueID())) {
            playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have any gang invites."));
            return;
        }

        PacketInviteToGang.gangInvites.remove(playerSender.getUniqueID());
        playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Gang invite declined."));
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
