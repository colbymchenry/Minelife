package com.minelife.gangs.server.commandhandlers;

import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.gangs.server.ICommandHandler;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

import java.util.Set;

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

        Estate estate = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

        if(estate == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be within an estate to set the gang's home."));
            return;
        }

        Set<Permission> permissions = estate.getPlayerPermissions(player.getUniqueID());

        if(!permissions.contains(Permission.ADD_MEMBER) && !permissions.contains(Permission.REMOVE_MEMBER) &&
                !permissions.contains(Permission.BREAK) && !permissions.contains(Permission.PLACE) && !permissions.contains(Permission.INTERACT) &&
                !permissions.contains(Permission.ENTER)) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to do that here."));
            return;
        }

        gang.setHome(player.worldObj, player.posX, player.posY + 0.1, player.posZ);
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Home set!"));
    }

}
