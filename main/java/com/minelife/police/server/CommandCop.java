package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.police.ModPolice;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class CommandCop extends CommandBase {

    @Override
    public String getName() {
        return "cop";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/cop addspawn\n/cop delspawn\n/cop reset";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        List<String> spawns = ModPolice.getConfig().getStringList("PoliceSpawnPoints") != null ? ModPolice.getConfig().getStringList("PoliceSpawnPoints") : Lists.newArrayList();

        if(args[0].equalsIgnoreCase("addspawn")) {
            spawns.add(sender.getPosition().getX() + "," + sender.getPosition().getY() + "," + sender.getPosition().getZ());
            sender.sendMessage(new TextComponentString("Spawn added!"));
        } else if(args[0].equalsIgnoreCase("delspawn")) {
            spawns.remove(sender.getPosition().getX() + "," + sender.getPosition().getY() + "," + sender.getPosition().getZ());
            sender.sendMessage(new TextComponentString("Spawn deleted!"));
        } else if(args[0].equalsIgnoreCase("reset")) {
            ServerProxy.cleanCops(sender.getEntityWorld());
            ServerProxy.spawnCops(sender.getEntityWorld());
            sender.sendMessage(new TextComponentString("Reset!"));
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }

        ModPolice.getConfig().set("PoliceSpawnPoints", spawns);
        ModPolice.getConfig().save();
    }
}
