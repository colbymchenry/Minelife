package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.police.ModPolice;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandPolice implements ICommand {

    @Override
    public String getCommandName()
    {
        return "police";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/police prison set | /police prison remove";
    }

    @Override
    public List getCommandAliases()
    {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String cmd = args[0];

        switch (cmd) {
            case "prison": {
                if (args.length == 1) {
                    sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
                } else {
                    if (args[1].equalsIgnoreCase("set")) {

                    } else if (args[1].equalsIgnoreCase("remove")) {
                        ModPolice.getServerProxy().setPrisonYard(null);
                    } else {
                        sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
                    }
                }
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return Lists.newArrayList();
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
