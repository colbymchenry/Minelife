package com.minelife.essentials.server.commands;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Whisper extends CommandBase {

    private static Map<UUID, UUID> MessagePairs = Maps.newHashMap();

    @Override
    public String getName() {
        return "whisper";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/whisper <player> <msg>";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("pm", "tell", "t");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(args[0]);

        if(Receiver == null) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        String msg = "";

        for (int i = 1; i < args.length; i++) {
            msg += args[i] + " ";
        }

        GameProfile gameprofile = server.getPlayerProfileCache().getGameProfileForUsername(sender.getName());

        if (gameprofile != null) {
            Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object) null);

            if (property == null) {
                gameprofile = server.getMinecraftSessionService().fillProfileProperties(gameprofile, true);
            }
        }

        // TODO
//        NotificationPM notificationPM = new NotificationPM(Receiver.getUniqueID(), msg, gameprofile);
//        notificationPM.sendTo(Receiver);

        MessagePairs.put(Receiver.getUniqueID(), ((EntityPlayerMP) sender).getUniqueID());
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "whisper");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    public static UUID GetLastSender(UUID PlayerUUID) {
        return MessagePairs.get(PlayerUUID);
    }

    public static void PutLastSender(UUID PlayerUUID, UUID Player2UUID) {
        MessagePairs.put(PlayerUUID, Player2UUID);
    }
}
