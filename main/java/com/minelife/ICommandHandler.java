package com.minelife;

import net.minecraft.command.ICommandSender;

public interface ICommandHandler {

    void execute(ICommandSender sender, String[] args);

    boolean isUsernameIndex(int index);
}
