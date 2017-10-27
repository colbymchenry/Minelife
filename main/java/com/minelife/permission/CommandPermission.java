package com.minelife.permission;

import com.minelife.util.server.MLCommand;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CommandPermission extends MLCommand {

    @Override
    public String getCommandName() {
        return "p";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return false;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {

    }
}
