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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Home implements ICommand {

    public Home() throws SQLException {
        ModEssentials.db.query("CREATE TABLE IF NOT EXISTS homes (uuid VARCHAR(36), world VARCHAR(100), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)");
    }

    @Override
    public String getCommandName() {
        return "home";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/home";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if(GetHome(Player.getUniqueID()) == null) {
            Player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have a home. Type /sethome to set your home."));
            return;
        }

        TeleportHandler.teleport(Player, GetHome(Player.getUniqueID()));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "home");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public static void SetHome(UUID UUID, Location Location) throws SQLException {
        if(GetHome(UUID) != null) {
            // update home
            ModEssentials.db.query("UPDATE homes SET world='" + Location.getWorld() + "'," +
                    " x='" + Location.getX() + "', y='" + Location.getY() + "', z='" + Location.getZ() + "', " +
                    "yaw='" + Location.getYaw() + "', pitch='" + Location.getPitch() + "' WHERE uuid='" + UUID.toString() + "'");
        } else {
            // set home
            ModEssentials.db.query("INSERT INTO homes (uuid, world, x, y, z, yaw, pitch) VALUES (" +
                    "'" + UUID.toString() + "', '" + Location.getWorld() + "', '" + Location.getX() + "', '" + Location.getY() + "', '" + Location.getZ() + "', " +
                    "'" + Location.getYaw() + "', '" + Location.getPitch() + "')");
        }
    }

    public static Location GetHome(UUID UUID) {
        try {
            ResultSet Result = ModEssentials.db.query("SELECT * FROM homes WHERE uuid='" + UUID.toString() + "'");
            if(Result.next()) {
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
