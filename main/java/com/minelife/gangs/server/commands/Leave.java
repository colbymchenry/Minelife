package com.minelife.gangs.server.commands;

import com.minelife.ICommandHandler;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Set;
import java.util.UUID;

public class Leave implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayerMP playerSender = (EntityPlayerMP) sender;
        Gang gang = ModGangs.getGang(playerSender.getUniqueID());

        if(gang == null) {
            playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not belong to a gang."));
            return;
        }

        if(gang.getLeader().equals(playerSender.getUniqueID())) {
            playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are the leader of your gang. You must assign leadership to another member before leaving."));
            return;
        }

        Set<UUID> officers = gang.getOfficers();
        Set<UUID> members = gang.getMembers();

        officers.remove(playerSender.getUniqueID());
        members.remove(playerSender.getUniqueID());

        gang.setMembers(members);
        gang.setOfficers(officers);
        playerSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You have left the gang!"));
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
