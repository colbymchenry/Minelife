package com.minelife.essentials.server.commands;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.essentials.NotificationPM;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Whisper implements ICommand {

    private static Map<UUID, UUID> MessagePairs = Maps.newHashMap();

    @Override
    public String getCommandName() {
        return "whisper";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/whisper <player> <msg>";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("pm", "tell", "t");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(args[0]);

        if(Receiver == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found."));
            return;
        }

        String msg = "";

        for (int i = 1; i < args.length; i++) {
            msg += args[i] + " ";
        }

        GameProfile gameprofile = MinecraftServer.getServer().func_152358_ax().func_152655_a(((EntityPlayerMP) sender).getCommandSenderName());

        if (gameprofile != null) {
            Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object) null);

            if (property == null) {
                gameprofile = MinecraftServer.getServer().func_147130_as().fillProfileProperties(gameprofile, true);
            }
        }

        NotificationPM notificationPM = new NotificationPM(Receiver.getUniqueID(), msg, gameprofile);
        notificationPM.sendTo(Receiver);

        MessagePairs.put(Receiver.getUniqueID(), ((EntityPlayerMP) sender).getUniqueID());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "whisper");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public static UUID GetLastSender(UUID PlayerUUID) {
        return MessagePairs.get(PlayerUUID);
    }

    public static void PutLastSender(UUID PlayerUUID, UUID Player2UUID) {
        MessagePairs.put(PlayerUUID, Player2UUID);
    }
}
