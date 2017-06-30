package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

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
        return "/claim\n" +
                "/claim sell\n" +
                "/claim auction\n" +
                "/claim buy";
    }

    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {

        if(args.length == 0) {

            return;
        }

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
