package com.minelife.jobs.job.farmer;

import com.minelife.jobs.EnumJob;
import com.minelife.jobs.server.CommandJob;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class CommandFarmer extends CommandBase {

    @Override
    public String getName() {
        return "farmer";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sender.sendMessage(new TextComponentString("/job farmer zone create|delete"));
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length != 3) {
            getUsage(sender);
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;

        if(args[1].equalsIgnoreCase("zone")) {
            Estate estate = ModRealEstate.getEstateAt(player.getEntityWorld(), player.getPosition());

            if(estate == null) {
                CommandJob.sendMessage(sender, EnumJob.FARMER, TextFormatting.RED + "Estate not found at your location.");
                return;
            }

            List<String> farmZones = FarmerHandler.INSTANCE.getConfig().getStringList("zones");


            if (args[2].equalsIgnoreCase("create")) {
                if(farmZones.contains(estate.getUniqueID().toString())) {
                    CommandJob.sendMessage(sender, EnumJob.FARMER, TextFormatting.RED + "Estate is already a farm zone.");
                    return;
                }
                farmZones.add(estate.getUniqueID().toString());
                CommandJob.sendMessage(player, EnumJob.FARMER, "Zone created!");
            } else if (args[2].equalsIgnoreCase("delete")) {
                if(!farmZones.contains(estate.getUniqueID().toString())) {
                    CommandJob.sendMessage(sender, EnumJob.FARMER, TextFormatting.RED + "Estate is not a farm zone.");
                    return;
                }
                farmZones.remove(estate.getUniqueID().toString());
                CommandJob.sendMessage(player, EnumJob.FARMER, "Zone delete!");
            }

            FarmerHandler.INSTANCE.getConfig().set("zones", farmZones);
            FarmerHandler.INSTANCE.getConfig().save();
        }
    }
}
