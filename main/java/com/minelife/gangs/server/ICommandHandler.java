package com.minelife.gangs.server;

import net.minecraft.command.ICommandSender;

public interface ICommandHandler {

    void execute(ICommandSender sender, String[] args);

}
