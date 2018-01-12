package com.minelife.essentials.server.commands;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.minelife.essentials.NotificationPM;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Reply implements ICommand {

    @Override
    public String getCommandName() {
        return "reply";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/reply <msg>";
    }

    @Override
    public List getCommandAliases() {
        List alias = Lists.newArrayList();
        alias.add("r");
        return alias;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        UUID ResponderUUID = Whisper.GetLastSender(Player.getUniqueID());

        if(ResponderUUID == null) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You have not received a message."));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(ResponderUUID);

        if(Receiver == null) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player could not be found or is not online anymore."));
            return;
        }

        String msg = "";

        for (int i = 0; i < args.length; i++) {
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

        Whisper.PutLastSender(Receiver.getUniqueID(), ((EntityPlayerMP) sender).getUniqueID());

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
}
