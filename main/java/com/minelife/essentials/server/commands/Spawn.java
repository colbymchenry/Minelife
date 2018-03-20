package com.minelife.essentials.server.commands;

import com.minelife.essentials.Location;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class Spawn extends CommandBase {

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/spawn";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(GetSpawn() == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "There is no spawn set on this server."));
            return;
        }

        TeleportHandler.teleport((EntityPlayerMP) sender, GetSpawn());
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "spawn");
    }

    public static void SetSpawn(Location Location) {
        ModEssentials.getConfig().set("spawn.world", Location.getWorld());
        ModEssentials.getConfig().set("spawn.x", Location.getX());
        ModEssentials.getConfig().set("spawn.y", Location.getY());
        ModEssentials.getConfig().set("spawn.z", Location.getZ());
        ModEssentials.getConfig().set("spawn.yaw", Location.getYaw());
        ModEssentials.getConfig().set("spawn.pitch", Location.getPitch());
        ModEssentials.getConfig().save();
    }

    public static Location GetSpawn() {
        if(ModEssentials.getConfig().get("spawn", null) != null) {
            String World = ModEssentials.getConfig().getString("spawn.world");
            double X = ModEssentials.getConfig().getDouble("spawn.x");
            double Y = ModEssentials.getConfig().getDouble("spawn.y");
            double Z = ModEssentials.getConfig().getDouble("spawn.z");
            float Yaw = (float) ModEssentials.getConfig().getDouble("spawn.yaw");
            float Pitch = (float) ModEssentials.getConfig().getDouble("spawn.pitch");
            return new Location(World, X, Y, Z, Yaw, Pitch);
        }

        return null;
    }
}