package com.minelife.economy.server;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandEconomy implements ICommand {

    @Override
    public String getCommandName()
    {
        return "economy";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return null;
    }

    @Override
    public List getCommandAliases()
    {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return false;
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
