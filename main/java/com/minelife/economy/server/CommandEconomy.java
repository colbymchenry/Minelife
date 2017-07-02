package com.minelife.economy.server;

import com.minelife.permission.ModPermission;
import com.minelife.permission.Player;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

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
        // TODO:
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if(!(sender instanceof EntityPlayer)) return true;

        Player player = ModPermission.get(((EntityPlayer) sender).getUniqueID());

        return player.hasPermission("economy");
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
