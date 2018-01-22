package com.minelife.gangs.server.commands;

import com.minelife.gangs.server.ICommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class Help implements ICommandHandler {

    @Override
    public void execute(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("/g create <name>"));
        sender.addChatMessage(new ChatComponentText("/g home"));
        sender.addChatMessage(new ChatComponentText("/g sethome"));
        sender.addChatMessage(new ChatComponentText("/g unsethome"));
    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
