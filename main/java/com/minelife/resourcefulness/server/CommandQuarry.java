package com.minelife.resourcefulness.server;

import com.minelife.realestate.server.SelectionListener;
import com.minelife.resourcefulness.quarry.Quarry;
import com.minelife.resourcefulness.quarry.QuarryListener;
import com.minelife.util.NumberConversions;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

public class CommandQuarry extends CommandBase  {

    @Override
    public String getName() {
        return "quarry";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/quarry create <reset-time-seconds>\n/quarry reload";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;

        if(args.length == 1) {
            QuarryListener.QUARRY_QUE.forEach(((quarry, resetTime) -> {
                try {
                    quarry.getConfig().reload();
                    player.sendMessage(new TextComponentString("Quarries reloaded!"));
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                    player.sendMessage(new TextComponentString("An error occurred."));
                }
            }));
            return;
        }

        if(args.length != 2) {
            player.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        if(!NumberConversions.isInt(args[1])) {
            player.sendMessage(new TextComponentString("Reset time must be an integer."));
            return;
        }

        if(!SelectionListener.hasFullSelection(player)) {
            player.sendMessage(new TextComponentString("Please make a full selection."));
            return;
        }

        BlockPos min = SelectionListener.getMinimum(player);
        BlockPos max = SelectionListener.getMaximum(player);

        try {
            Quarry quarry = new Quarry(player.dimension, Integer.parseInt(args[1]), min, max, player.getPosition());
            QuarryListener.QUARRY_QUE.put(quarry, System.currentTimeMillis());
            player.sendMessage(new TextComponentString("Quarry created."));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            player.sendMessage(new TextComponentString("An error occurred."));
        }
    }

}
