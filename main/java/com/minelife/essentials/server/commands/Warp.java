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

public class Warp extends CommandBase {

    // TODO: These are all wrong, this is warps for the player. Need to make warps for everyone, and allow players to set multiple homes only for themselves
    public Warp() throws SQLException {
        ModEssentials.getDB().query("CREATE TABLE IF NOT EXISTS warps (name VARCHAR(20), dimension INT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)");
    }

    public static Map<String, Location> GetWarps() throws SQLException {
        Map<String, Location> Warps = Maps.newHashMap();
        ResultSet result = ModEssentials.getDB().query("SELECT * FROM warps");
        while (result.next()) {
            Location loc = new Location(result.getInt("dimension"), result.getDouble("x"), result.getDouble("y"), result.getDouble("z"));
            loc.setPitch(result.getFloat("pitch"));
            loc.setYaw(result.getFloat("yaw"));
            Warps.put(result.getString("name"), loc);
        }

        return Warps;
    }

    public static void CreateWarp(String Name, Location Location) throws SQLException {
        ModEssentials.getDB().query("INSERT INTO warps (name, dimension, x, y, z, yaw, pitch) VALUES " +
                "('" + Name.toLowerCase() + "', '" + Location.getDimension() + "', " +
                "'" + Location.getX() + "', '" + Location.getY() + "', '" + Location.getZ() + "', '" + Location.getYaw() + "'," +
                " '" + Location.getPitch() + "')");
    }

    public static void DeleteWarp(String Name) throws SQLException {
        ModEssentials.getDB().query("DELETE FROM warps WHERE name='" + Name.toLowerCase() + "'");
    }

    @Override
    public String getName() {
        return "warp";
    }

    @Override
    public List<String> getAliases() {
        return Lists.newArrayList("warps");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/warp <name>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        try {
            Map<String, Location> Warps = GetWarps();

            // if they type JUST /warp send them a list of their current warps
            if (args.length == 0) {
                Player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&c=-=-=-=[&6WARPS&c]=-=-=-=", '&')));
                int i = 1;
                for (String s : Warps.keySet()) {
                    Player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&c" + i + ". &6" + s, '&')));
                    i++;
                }
                return;
            }


            String WarpName = args[0].toLowerCase();

            if (!Warps.containsKey(WarpName)) {
                Player.sendMessage(new TextComponentString(TextFormatting.RED + "Warp not found. Type '/warps' to view warps."));
                return;
            }

            Location Location = Warps.get(WarpName);

            TeleportHandler.teleport(Player, Location);
        } catch (SQLException e) {
            e.printStackTrace();
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "warp");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        if (!(sender instanceof EntityPlayerMP)) return Lists.newArrayList();

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if (args.length == 1) {
            try {
                Map<String, Location> Warps = GetWarps();
                List<String> WarpList = Lists.newArrayList();
                WarpList.addAll(Warps.keySet());
                return WarpList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return Lists.newArrayList();
    }

}
