package com.minelife.gangs.server.commands;

import com.minelife.gangs.ModGangs;
import com.minelife.gangs.server.ICommandHandler;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.realestate.Selection;
import com.minelife.realestate.server.SelectionHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;

import java.util.Set;

public class Vault implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;
        Selection selection = SelectionHandler.getSelection(Player);

        if(selection.getMin() == null || selection.getMax() == null) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please make a full selection."));
            return;
        }

        Estate estate = EstateHandler.loadedEstates.stream().filter(e -> e.contains(selection)).findFirst().orElse(null);

        if(estate == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Selection must be within an estate owned by the gang."));
            return;
        }

        Set<Permission> permissions = estate.getPlayerPermissions(Player.getUniqueID());

        if(!permissions.contains(Permission.ADD_MEMBER) && !permissions.contains(Permission.REMOVE_MEMBER) &&
                !permissions.contains(Permission.BREAK) && !permissions.contains(Permission.PLACE) && !permissions.contains(Permission.INTERACT) &&
                !permissions.contains(Permission.ENTER)) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to do that here."));
            return;
        }


    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
