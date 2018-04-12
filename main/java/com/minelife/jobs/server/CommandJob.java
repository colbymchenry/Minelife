package com.minelife.jobs.server;

import com.google.common.collect.Maps;
import com.minelife.jobs.job.farmer.CommandFarmer;
import com.minelife.jobs.server.commands.CommandNPC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.Map;

public class CommandJob extends CommandBase {

    private static Map<String, CommandBase> subCommands = Maps.newHashMap();

    static {
        subCommands.put("npc", new CommandNPC());
        subCommands.put("farmer", new CommandFarmer());
    }

    @Override
    public String getName() {
        return "job";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof EntityPlayer)) return;

        if(args.length == 0) return;

        if(!subCommands.containsKey(args[0].toLowerCase())) {
            return;
        }

        subCommands.get(args[0].toLowerCase()).execute(server, sender, args);
    }
}
