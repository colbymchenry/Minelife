package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.police.ModPolice;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.List;

public class CommandPrison extends CommandBase {

    @Override
    public String getName() {
        return "prison";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/prison create\n/prison dropoff\n/prison regroup";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        Estate estate = ModRealEstate.getEstateAt(sender.getEntityWorld(), sender.getPosition());
        Prison prison = Prison.getPrison(sender.getPosition());

        if (args[0].equalsIgnoreCase("create")) {
            if (estate == null) {
                sender.sendMessage(new TextComponentString("There is no estate here."));
                return;
            }

            if (prison != null) {
                sender.sendMessage(new TextComponentString("There is already a prison define here."));
                return;
            }

            try {
                new Prison(estate.getUniqueID());
                sender.sendMessage(new TextComponentString("Prison created."));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
                sender.sendMessage(new TextComponentString("Something went wrong."));
            }
        } else if (args[0].equalsIgnoreCase("dropoff")) {
            if (prison == null) {
                sender.sendMessage(new TextComponentString("There is no prison here."));
                return;
            }

            prison.setDropOffPos(sender.getPosition());
            sender.sendMessage(new TextComponentString("Drop off position set."));
        } else if (args[0].equalsIgnoreCase("regroup")) {
            List<String> regroups = Lists.newArrayList();
            if(ModPolice.getConfig().contains("regroup")) regroups = ModPolice.getConfig().getStringList("regroup");
            regroups.add( sender.getPosition().getX() + "," + sender.getPosition().getY() + "," + sender.getPosition().getZ());
            ModPolice.getConfig().set("regroup", regroups);
            ModPolice.getConfig().save();
            sender.sendMessage(new TextComponentString("Regroup position added!"));
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }
}
