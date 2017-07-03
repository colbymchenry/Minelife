package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandClaim implements ICommand {

    @Override
    public String getCommandName()
    {
        return "claim";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/claim";
    }

    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (canCommandSenderUseCommand(sender))
            Minelife.NETWORK.sendTo(new PacketOpenGui(ModRealEstate.getPricePerChunk()), (EntityPlayerMP) sender);
        else
            sender.addChatMessage(new ChatComponentText("Only players can use this command."));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return sender instanceof EntityPlayerMP;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

}