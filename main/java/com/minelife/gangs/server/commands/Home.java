package com.minelife.gangs.server.commands;

import com.minelife.essentials.TeleportHandler;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.gangs.server.ICommandHandler;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.util.Location;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

import java.util.Set;

public class Home implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayer player = (EntityPlayer) sender;

        Gang gang = ModGangs.getPlayerGang(player.getUniqueID());

        if(gang == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not in a gang."));
            return;
        }

        if(gang.getHome() == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "There is no home set for your gang."));
            return;
        }

        Estate estate = EstateHandler.getEstateAt(gang.getHome().getEntityWorld(),
                Vec3.createVectorHelper(gang.getHome().getX(), gang.getHome().getY(), gang.getHome().getZ()));

        if(estate == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Your gang's home must be within an estate."));
            return;
        }

        Set<Permission> permissions = estate.getPlayerPermissions(gang.getLeader());

        if(!permissions.contains(Permission.ADD_MEMBER) && !permissions.contains(Permission.REMOVE_MEMBER) &&
                !permissions.contains(Permission.BREAK) && !permissions.contains(Permission.PLACE) && !permissions.contains(Permission.INTERACT) &&
                !permissions.contains(Permission.ENTER)) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Your gang has lost permission to use '/g home' at its current location."));
            return;
        }

        TeleportHandler.teleport((EntityPlayerMP) player, gang.getHome());
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }

}
