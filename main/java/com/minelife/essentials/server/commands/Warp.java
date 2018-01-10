package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.permission.ModPermission;
import com.minelife.util.Location;
import com.minelife.util.StringHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Warp implements ICommand {

    public Warp() throws SQLException {
        ModEssentials.db.query("CREATE TABLE IF NOT EXISTS warps (player_uuid VARCHAR(36), name VARCHAR(20), world VARCHAR(20), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)");
    }

    public static Map<String, Location> GetWarps(UUID PlayerUUID) throws SQLException {
        Map<String, Location> Warps = Maps.newHashMap();
        ResultSet result = ModEssentials.db.query("SELECT * FROM warps WHERE player_uuid='" + PlayerUUID.toString() + "'");
        while (result.next()) {
            Location loc = new Location(result.getString("world"), result.getDouble("x"), result.getDouble("y"), result.getDouble("z"));
            loc.setPitch(result.getFloat("pitch"));
            loc.setYaw(result.getFloat("yaw"));
            Warps.put(result.getString("name"), loc);
        }

        return Warps;
    }

    public static void CreateWarp(String Name, Location Location, UUID PlayerUUID) throws SQLException {
        ModEssentials.db.query("INSERT INTO warps (player_uuid, name, world, x, y, z, yaw, pitch) VALUES " +
                "('" + PlayerUUID.toString() + "', '" + Name.toLowerCase() + "', '" + Location.getWorld() + "', " +
                "'" + Location.getX() + "', '" + Location.getY() + "', '" + Location.getZ() + "', '" + Location.getYaw() + "'," +
                " '" + Location.getPitch() + "')");
    }

    public static void DeleteWarp(String Name, UUID PlayerUUID) throws SQLException {
        ModEssentials.db.query("DELETE FROM warps WHERE player_uuid='" + PlayerUUID.toString() + "' AND name='" + Name.toLowerCase() + "'");
    }

    @Override
    public String getCommandName() {
        return "warp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/warp <name>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        try {
            Map<String, Location> Warps = GetWarps(Player.getUniqueID());

            // if they type JUST /warp send them a list of their current warps
            if (args.length == 0) {
                if(Warps.isEmpty()) {
                    Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You have no warps. Type /setwarp <name> to create a warp."));
                } else {
                    Player.addChatMessage(new ChatComponentText(StringHelper.ParseFormatting("&c=-=-=-=[&6WARPS&c]=-=-=-=", '&')));
                    int i = 1;
                    for (String s : Warps.keySet()) {
                        Player.addChatMessage(new ChatComponentText(StringHelper.ParseFormatting("&c" + i + ". &6" + s, '&')));
                        i++;
                    }
                }
                return;
            }


            String WarpName = args[0].toLowerCase();

            if(!Warps.containsKey(WarpName)) {
                Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Warp not found. Type '/warp' to view your warps."));
                return;
            }

            Location Location = Warps.get(WarpName);

            TeleportHandler.teleport(Player, Location);
        } catch (SQLException e) {
            e.printStackTrace();
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occurred."));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "warp");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) return Lists.newArrayList();

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if (args.length == 1) {
            try {
                Map<String, Location> Warps = GetWarps(Player.getUniqueID());
                List<String> WarpList = Lists.newArrayList();
                WarpList.addAll(Warps.keySet());
                WarpList.add("list");
                return WarpList;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
