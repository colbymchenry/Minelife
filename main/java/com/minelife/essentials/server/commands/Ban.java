package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.essentials.ModEssentials;
import com.minelife.permission.ModPermission;
import com.minelife.util.DateHelper;
import com.minelife.util.PlayerHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Ban extends MLCommand {

    public Ban() throws SQLException {
        ModEssentials.db.query("CREATE TABLE IF NOT EXISTS banned_uuids (uuid VARCHAR(36), unbanDate VARCHAR(100))");
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public String getCommandName() {
        return "ban";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ban <player>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayer) ? true : ModPermission.hasPermission(((EntityPlayer) sender).getUniqueID(), "ban");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Fetching player's UUID..."));
        UUID playerUUID = UUIDFetcher.get(args[0]);

        if (playerUUID == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found."));
            return;
        }

        BanPlayer(playerUUID, 0);

        if (PlayerHelper.getPlayer(playerUUID) != null) {
            PlayerHelper.getPlayer(playerUUID).playerNetServerHandler.kickPlayerFromServer("You are banished from this server.");
        }

        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Player banned."));
    }

    public static void BanPlayer(UUID playerUUID, int seconds) throws SQLException {
        UnBanPlayer(playerUUID);

        if (seconds == 0) {
            ModEssentials.db.query("INSERT INTO banned_uuids (uuid, unbanDate) VALUES ('" + playerUUID.toString() + "', '0')");
        } else {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.SECOND, seconds);
            ModEssentials.db.query("INSERT INTO banned_uuids (uuid, unbanDate) VALUES ('" + playerUUID.toString() + "', '" + DateHelper.DateToString(now.getTime()) + "')");
        }
    }

    public static void UnBanPlayer(UUID PlayerUUID) throws SQLException {
        ModEssentials.db.query("DELETE FROM banned_uuids WHERE uuid='" + PlayerUUID.toString() + "'");
    }

    public static boolean IsPlayerBanned(UUID PlayerUUID) {
        try {
            ResultSet result = ModEssentials.db.query("SELECT * FROM banned_uuids WHERE uuid='" + PlayerUUID.toString() + "'");
            if (result.next()) {
                if (result.getString("unbanDate").equals("0")) return true;
                Date date = DateHelper.StringToDate(result.getString("unbanDate"));
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
            ResultSet result = ModEssentials.db.query("SELECT * FROM banned_uuids WHERE uuid='" + PlayerUUID.toString() + "'");
            if (result.next()) {
                if (result.getString("unbanDate").equals("0")) return null;
                Date date = DateHelper.StringToDate(result.getString("unbanDate"));
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
                ((EntityPlayerMP) event.player).playerNetServerHandler.kickPlayerFromServer("You are banished from this server.");
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

            ((EntityPlayerMP) event.player).playerNetServerHandler.kickPlayerFromServer("You are banned for " + elapsedHours + " hours, " + elapsedMinutes + " minutes, and " + elapsedSeconds + " second(s).");
        } else UnBanPlayer(event.player.getUniqueID());
    }
}
