package com.minelife.gangs.server;

import com.minelife.util.server.MLCommand;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;

public class CommandGang extends MLCommand {

    @Override
    public String getCommandName() {
        return "gang";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("gangs", "g");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
    }
}
