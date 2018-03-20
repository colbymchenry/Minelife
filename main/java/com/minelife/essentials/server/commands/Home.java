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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Home extends CommandBase {

    public Home() throws SQLException {
        ModEssentials.getDB().query("CREATE TABLE IF NOT EXISTS homes (uuid VARCHAR(36), world VARCHAR(100), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)");
    }

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/home";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if (GetHome(Player.getUniqueID()) == null) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a home. Type /sethome to set your home."));
            return;
        }

        TeleportHandler.teleport(Player, GetHome(Player.getUniqueID()));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "home");
    }

    public static void SetHome(UUID UUID, Location Location) throws SQLException {
        if (GetHome(UUID) != null) {
            // update home
            ModEssentials.getDB().query("UPDATE homes SET world='" + Location.getWorld() + "'," +
                    " x='" + Location.getX() + "', y='" + Location.getY() + "', z='" + Location.getZ() + "', " +
                    "yaw='" + Location.getYaw() + "', pitch='" + Location.getPitch() + "' WHERE uuid='" + UUID.toString() + "'");
        } else {
            // set home
            ModEssentials.getDB().query("INSERT INTO homes (uuid, world, x, y, z, yaw, pitch) VALUES (" +
                    "'" + UUID.toString() + "', '" + Location.getWorld() + "', '" + Location.getX() + "', '" + Location.getY() + "', '" + Location.getZ() + "', " +
                    "'" + Location.getYaw() + "', '" + Location.getPitch() + "')");
        }
    }

    public static Location GetHome(UUID UUID) {
        try {
            ResultSet Result = ModEssentials.getDB().query("SELECT * FROM homes WHERE uuid='" + UUID.toString() + "'");
            if (Result.next()) {
                String World = Result.getString("world");
                double x = Result.getDouble("x");
                double y = Result.getDouble("y");
                double z = Result.getDouble("z");
                float Yaw = Result.getFloat("yaw");
                float Pitch = Result.getFloat("pitch");

                return new Location(World, x, y, z, Yaw, Pitch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
