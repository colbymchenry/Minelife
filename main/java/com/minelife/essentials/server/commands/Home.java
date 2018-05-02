package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.essentials.Location;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.permission.ModPermission;
import com.minelife.util.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Home extends CommandBase {

    public Home() throws SQLException {
        ModEssentials.getDB().query("CREATE TABLE IF NOT EXISTS homes (player VARCHAR(36), name VARCHAR(100), dimension INT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)");
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
        Map<String, Location> homes = GetHomes(Player.getUniqueID());

        if (homes.isEmpty()) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a home. Sleep in a bed to set your home."));
            return;
        }

        String id = args.length == 0 ? "default" : args[0].toLowerCase();

        if(!ModPermission.hasPermission(Player.getUniqueID(), "homes.admin")) id = "default";

        if (!homes.containsKey(id)) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a home. Sleep in a bed to set your home."));
            return;
        }

        Location location = homes.get(id);
        location.setX(location.getX() + 0.5);
        location.setY(location.getY() + 0.2);
        location.setZ(location.getZ() + 0.5);

        TeleportHandler.teleport(Player, location);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "home");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (!(sender instanceof EntityPlayerMP)) return Lists.newArrayList();

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if (args.length == 1) {
            Map<String, Location> Homes = GetHomes(Player.getUniqueID());
            List<String> WarpList = Lists.newArrayList();
            WarpList.addAll(Homes.keySet());
            return WarpList;
        }

        return Lists.newArrayList();
    }


    public static void SetHome(String name, Location Location, UUID playerID) throws SQLException {
        ModEssentials.getDB().query("INSERT INTO homes (player, name, dimension, x, y, z, yaw, pitch) VALUES (" +
                "'" + playerID.toString() + "', '" + name.toLowerCase() + "', '" + Location.getDimension() + "', '" + Location.getX() + "', '" + Location.getY() + "', '" + Location.getZ() + "', " +
                "'" + Location.getYaw() + "', '" + Location.getPitch() + "')");

    }

    public static void DelHome(String name, UUID player) {
        try {
            ModEssentials.getDB().query("DELETE FROM homes WHERE name='" + name + "' AND player='" + player.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Location> GetHomes(UUID playerID) {
        Map<String, Location> homes = Maps.newHashMap();
        try {
            ResultSet Result = ModEssentials.getDB().query("SELECT * FROM homes WHERE player='" + playerID.toString() + "'");
            while (Result.next()) {
                int dimension = Result.getInt("dimension");
                double x = Result.getDouble("x");
                double y = Result.getDouble("y");
                double z = Result.getDouble("z");
                float Yaw = Result.getFloat("yaw");
                float Pitch = Result.getFloat("pitch");
                homes.put(Result.getString("name"), new Location(dimension, x, y, z, Yaw, Pitch));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return homes;
    }

    public static UUID getHomeAtLoc(int dimension, BlockPos pos) {
        try {
            ResultSet result = ModEssentials.getDB().query("SELECT * FROM homes WHERE dimension='" + dimension + "' AND x='" + pos.getX() + "' AND y='" + pos.getY()+ "' AND z='" + pos.getZ() + "'");
            if(result.next()) {
                return UUID.fromString(result.getString("player"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
