package com.minelife.gangs.server.commands;

import com.minelife.gangs.Gang;
import com.minelife.gangs.network.PacketInviteToGang;
import com.minelife.ICommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Set;
import java.util.UUID;

public class Accept implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayerMP playerSender = (EntityPlayerMP) sender;

        if(!PacketInviteToGang.gangInvites.containsKey(playerSender.getUniqueID())) {
            playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have any gang invites."));
            return;
        }

        Gang gang = PacketInviteToGang.gangInvites.get(playerSender.getUniqueID());
        Set<UUID> members = gang.getMembers();
        members.add(playerSender.getUniqueID());
        gang.setMembers(members);

        playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Gang invite accepted!"));
        PacketInviteToGang.gangInvites.remove(playerSender.getUniqueID());
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
