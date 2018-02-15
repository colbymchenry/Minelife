package com.minelife.gangs.server.commands;

import com.minelife.gangs.server.ICommandHandler;
import net.minecraft.command.ICommandSender;

public class Accept implements ICommandHandler {

    // TODO:
    @Override
    public void execute(ICommandSender sender, String[] args) {

    }

    @Override
    public boolean isUsernameIndex(int index) {
        return false;
    }
}
