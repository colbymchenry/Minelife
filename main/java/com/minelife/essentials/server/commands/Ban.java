package com.minelife.essentials.server.commands;

import com.minelife.essentials.ModEssentials;
import com.minelife.permission.ModPermission;
import com.minelife.util.DateHelper;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Ban extends MLCommand {

    public Ban() throws SQLException {
        ModEssentials.getDB().query("CREATE TABLE IF NOT EXISTS banned_uuids (uuid VARCHAR(36), unbanDate VARCHAR(100))");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ban <player>";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return !(sender instanceof EntityPlayer) || ModPermission.hasPermission(((EntityPlayer) sender).getUniqueID(), "ban");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Fetching player's UUID..."));
        UUID playerUUID = UUIDFetcher.get(args[0]);

        if (playerUUID == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        BanPlayer(playerUUID, 0);

        if (PlayerHelper.getPlayer(playerUUID) != null) {
            PlayerHelper.getPlayer(playerUUID).connection.disconnect(new TextComponentString("You are banished from this server."));
        }

        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Player banned."));
    }

    public static void BanPlayer(UUID playerUUID, int seconds) throws SQLException {
        UnBanPlayer(playerUUID);

        if (seconds == 0) {
            ModEssentials.getDB().query("INSERT INTO banned_uuids (uuid, unbanDate) VALUES ('" + playerUUID.toString() + "', '0')");
        } else {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.SECOND, seconds);
            ModEssentials.getDB().query("INSERT INTO banned_uuids (uuid, unbanDate) VALUES ('" + playerUUID.toString() + "', '" + DateHelper.dateToString(now.getTime()) + "')");
        }
    }

    public static void UnBanPlayer(UUID PlayerUUID) throws SQLException {
        ModEssentials.getDB().query("DELETE FROM banned_uuids WHERE uuid='" + PlayerUUID.toString() + "'");
    }

    public static boolean IsPlayerBanned(UUID PlayerUUID) {
        try {
            ResultSet result = ModEssentials.getDB().query("SELECT * FROM banned_uuids WHERE uuid='" + PlayerUUID.toString() + "'");
            if (result.next()) {
                if (result.getString("unbanDate").equals("0")) return true;
                Date date = DateHelper.stringToDate(result.getString("unbanDate"));
                return date.after(Calendar.getInstance().getTime());
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Date GetUnbanDate(UUID PlayerUUID) {
        try {
            ResultSet result = ModEssentials.getDB().query("SELECT * FROM banned_uuids WHERE uuid='" + PlayerUUID.toString() + "'");
            if (result.next()) {
                if (result.getString("unbanDate").equals("0")) return null;
                Date date = DateHelper.stringToDate(result.getString("unbanDate"));
                return date;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) throws SQLException {
        if (IsPlayerBanned(event.player.getUniqueID())) {

            Date endDate = GetUnbanDate(event.player.getUniqueID());

            if (endDate == null) {
                ((EntityPlayerMP) event.player).connection.disconnect(new TextComponentString("You are banished from this server."));
                return;
            }

            long different = endDate.getTime() - Calendar.getInstance().getTime().getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            //long elapsedDays = different / daysInMilli;
            //different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            ((EntityPlayerMP) event.player).connection.disconnect(new TextComponentString("You are banned for " + elapsedHours + " hours, " + elapsedMinutes + " minutes, and " + elapsedSeconds + " second(s)."));
        } else UnBanPlayer(event.player.getUniqueID());
    }
}
