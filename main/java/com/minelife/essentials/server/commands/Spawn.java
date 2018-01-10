package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.permission.ModPermission;
import com.minelife.util.Location;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class Spawn implements ICommand {

    @Override
    public String getCommandName() {
        return "spawn";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/spawn";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(GetSpawn() == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "There is no spawn set on this server."));
            return;
        }

        TeleportHandler.teleport((EntityPlayerMP) sender, GetSpawn());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "spawn");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public static void SetSpawn(Location Location) {
        ModEssentials.config.set("spawn.world", Location.getWorld());
        ModEssentials.config.set("spawn.x", Location.getX());
        ModEssentials.config.set("spawn.y", Location.getY());
        ModEssentials.config.set("spawn.z", Location.getZ());
        ModEssentials.config.set("spawn.yaw", Location.getYaw());
        ModEssentials.config.set("spawn.pitch", Location.getPitch());
        ModEssentials.config.save();
    }

    public static Location GetSpawn() {
        if(ModEssentials.config.get("spawn", null) != null) {
            String World = ModEssentials.config.getString("spawn.world");
            double X = ModEssentials.config.getDouble("spawn.x");
            double Y = ModEssentials.config.getDouble("spawn.y");
            double Z = ModEssentials.config.getDouble("spawn.z");
            float Yaw = (float) ModEssentials.config.getDouble("spawn.yaw");
            float Pitch = (float) ModEssentials.config.getDouble("spawn.pitch");
            return new Location(World, X, Y, Z, Yaw, Pitch);
        }

        return null;
    }
}