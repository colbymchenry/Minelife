package com.minelife.gangs.server.commands;

import com.minelife.ICommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Help implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("/g create <name>"));
        sender.addChatMessage(new ChatComponentText("/g home"));
        sender.addChatMessage(new ChatComponentText("/g sethome"));
        sender.addChatMessage(new ChatComponentText("/g unsethome"));
        sender.addChatMessage(new ChatComponentText( EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD.toString() + "/g " + EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.BOLD.toString() + "- opens the gang GUI."));;
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
