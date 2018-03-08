package com.minelife.gangs.server.commands;

import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.ICommandHandler;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

public class SetHome implements ICommandHandler{

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayer player = (EntityPlayer) sender;

        Gang gang = ModGangs.getPlayerGang(player.getUniqueID());

        if(gang == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not in a gang."));
            return;
        }

        if(!gang.getLeader().equals(player.getUniqueID()) && !gang.getOfficers().contains(player.getUniqueID())) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be an officer or the leader to set the home of the gang."));
            return;
        }

        Estate estate = EstateHandler.getEstateAt(player.worldObj,player.posX, player.posY, player.posZ);

        if(estate == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be within an estate to set the gang's home."));
            return;
        }

        if (estate.getOwner() == null || gang.getLeader() == null || !estate.getOwner().equals(gang.getLeader())) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission. Gang leader must also be the owner of the estate."));
            return;
        }

        gang.setHome(player.worldObj, player.posX, player.posY + 0.1, player.posZ);
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Home set!"));
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }

}
