package com.minelife.gangs.server.commands;

import com.minelife.ICommandHandler;
import com.minelife.essentials.TeleportHandler;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

import java.util.Objects;

public class Disband implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayer player = (EntityPlayer) sender;

        Gang gang = ModGangs.getPlayerGang(player.getUniqueID());

        if(gang == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not in a gang."));
            return;
        }

        if(!Objects.equals(gang.getLeader(), player.getUniqueID())) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You must be the leader to disband the gang."));
            return;
        }

        if(gang.disband()) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Gang disbanded..."));
        } else {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Error: Could not disband gang. Please notify an admin."));
        }
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
