package com.minelife.essentials.server.commands;

import com.minelife.essentials.Location;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.server.FMLServerHandler;

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
        ModEssentials.getConfig().set("spawn.dimension", Location.getDimension());
        ModEssentials.getConfig().set("spawn.x", Location.getX());
        ModEssentials.getConfig().set("spawn.y", Location.getY());
        ModEssentials.getConfig().set("spawn.z", Location.getZ());
        ModEssentials.getConfig().set("spawn.yaw", Location.getYaw());
        ModEssentials.getConfig().set("spawn.pitch", Location.getPitch());
        ModEssentials.getConfig().save();
    }

    public static Location GetSpawn() {
        if(ModEssentials.getConfig().get("spawn", null) != null) {
            int dimension = ModEssentials.getConfig().getInt("spawn.dimension");
            double X = ModEssentials.getConfig().getDouble("spawn.x");
            double Y = ModEssentials.getConfig().getDouble("spawn.y");
            double Z = ModEssentials.getConfig().getDouble("spawn.z");
            float Yaw = (float) ModEssentials.getConfig().getDouble("spawn.yaw");
            float Pitch = (float) ModEssentials.getConfig().getDouble("spawn.pitch");
            return new Location(dimension, X, Y, Z, Yaw, Pitch);
        }

        BlockPos defaultPos = FMLServerHandler.instance().getServer().worlds[0].getSpawnCoordinate();

        Location defaultLoc = new Location(FMLServerHandler.instance().getServer().worlds[0].provider.getDimension(), defaultPos.getX() + 0.5, defaultPos.getY() + 0.5, defaultPos.getZ() + 0.5);

        return defaultLoc;
    }

    public static void SetNewSpawn(Location Location) {
        ModEssentials.getConfig().set("spawnnew.dimension", Location.getDimension());
        ModEssentials.getConfig().set("spawnnew.x", Location.getX());
        ModEssentials.getConfig().set("spawnnew.y", Location.getY());
        ModEssentials.getConfig().set("spawnnew.z", Location.getZ());
        ModEssentials.getConfig().set("spawnnew.yaw", Location.getYaw());
        ModEssentials.getConfig().set("spawnnew.pitch", Location.getPitch());
        ModEssentials.getConfig().save();
    }

    public static Location GetNewSpawn() {
        if(ModEssentials.getConfig().get("spawnnew", null) != null) {
            int dimension = ModEssentials.getConfig().getInt("spawnnew.dimension");
            double X = ModEssentials.getConfig().getDouble("spawnnew.x");
            double Y = ModEssentials.getConfig().getDouble("spawnnew.y");
            double Z = ModEssentials.getConfig().getDouble("spawnnew.z");
            float Yaw = (float) ModEssentials.getConfig().getDouble("spawnnew.yaw");
            float Pitch = (float) ModEssentials.getConfig().getDouble("spawnnew.pitch");
            return new Location(dimension, X, Y, Z, Yaw, Pitch);
        }

        return GetSpawn();
    }
}