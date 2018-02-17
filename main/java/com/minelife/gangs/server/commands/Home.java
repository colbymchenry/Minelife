package com.minelife.gangs.server.commands;

import com.minelife.essentials.TeleportHandler;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.ICommandHandler;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

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

        if (estate.getOwner() == null || gang.getLeader() == null || !estate.getOwner().equals(gang.getLeader())) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission. Gang leader must also be the owner of the estate."));
            return;
        }
        TeleportHandler.teleport((EntityPlayerMP) player, gang.getHome());
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }

}
