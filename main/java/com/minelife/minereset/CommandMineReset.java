package com.minelife.minereset;

import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.NumberConversions;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.UUID;

public class CommandMineReset extends CommandBase  {

    @Override
    public String getName() {
        return "minereset";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/minereset <duration>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 1) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            try {
                ModMineReset.getConfig().reload();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;

        Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

        if(estate == null) {
            player.sendMessage(new TextComponentString("There is no estate here."));
            return;
        }

        if(!NumberConversions.isInt(args[0])) {
            sender.sendMessage(new TextComponentString("Duration must be an integer."));
            return;
        }

        int duration = NumberConversions.toInt(args[0]);

        if(duration < 20) {
            sender.sendMessage(new TextComponentString("Duration must be greater than 20 minutes."));
            return;
        }

        ModMineReset.getConfig().set("estateID", estate.getUniqueID().toString());
        ModMineReset.getConfig().set("duration", duration);
        ModMineReset.getConfig().save();
        ServerProxy.mine = new Mine(UUID.fromString(ModMineReset.getConfig().getString("estateID", null)), ModMineReset.getConfig().getInt("duration", 0));
        sender.sendMessage(new TextComponentString("Created!"));
    }
}
