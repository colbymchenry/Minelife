package com.minelife.gangs.server.commands;

import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.gangs.server.ICommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class UnsetHome implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayer player = (EntityPlayer) sender;

        Gang gang = ModGangs.getPlayerGang(player.getUniqueID());

        if(gang == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not in a gang."));
            return;
        }

        if(!gang.getLeader().equals(player.getUniqueID()) && !gang.getOfficers().contains(player.getUniqueID())) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be an officer or the leader to unset the home of the gang."));
            return;
        }

        gang.removeHome();
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Home unset!"));
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }

}
