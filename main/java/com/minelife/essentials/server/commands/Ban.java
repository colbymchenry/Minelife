package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.essentials.ModEssentials;
import com.minelife.permission.ModPermission;
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
import java.util.List;
import java.util.UUID;

public class Ban extends MLCommand {

    public Ban() throws SQLException {
        ModEssentials.db.query("CREATE TABLE IF NOT EXISTS banned_uuids (uuid VARCHAR(36))");
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

        banPlayer(playerUUID);

        if (PlayerHelper.getPlayer(playerUUID) != null) {
            PlayerHelper.getPlayer(playerUUID).playerNetServerHandler.kickPlayerFromServer("You are banished from this server.");
        }

        try {
            banPlayer(playerUUID);
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Player banned."));
        } catch (SQLException e) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error occurred."));
            e.printStackTrace();
        }
    }

    public static void banPlayer(UUID playerUUID) throws SQLException {
        if (isPlayerBanned(playerUUID)) return;
        ModEssentials.db.query("INSERT INTO banned_uuids (uuid) VALUES ('" + playerUUID.toString() + "')");
    }

    public static boolean isPlayerBanned(UUID playerUUID) {
        try {
            ResultSet result = ModEssentials.db.query("SELECT * FROM banned_uuids WHERE uuid='" + playerUUID.toString() + "'");
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void UnBanPlayer(UUID PlayerUUID) throws SQLException {
        ModEssentials.db.query("DELETE FROM banned_uuids WHERE uuid='" + PlayerUUID.toString() + "'");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (isPlayerBanned(event.player.getUniqueID()))
            ((EntityPlayerMP) event.player).playerNetServerHandler.kickPlayerFromServer("You are banished from this server.");
    }
}
