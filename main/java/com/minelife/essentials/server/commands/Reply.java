package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.UUID;

public class Reply extends CommandBase {

    @Override
    public String getName() {
        return "reply";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/reply <msg>";
    }

    @Override
    public List<String> getAliases() {
        List alias = Lists.newArrayList();
        alias.add("r");
        return alias;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        EntityPlayerMP Player = (EntityPlayerMP) sender;

        UUID ResponderUUID = Whisper.GetLastSender(Player.getUniqueID());

        if(ResponderUUID == null) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "You have not received a message."));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(ResponderUUID);

        if(Receiver == null) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "Player could not be found or is not online anymore."));
            return;
        }

        String msg = "";

        for (int i = 0; i < args.length; i++) msg += args[i] + " ";

        Notification notification = new Notification(Receiver.getUniqueID(), TextFormatting.DARK_GREEN + Player.getName() + "\n" + TextFormatting.DARK_GRAY + msg, NotificationType.EDGED, 10, 0xFFFFFF);
        notification.sendTo(Receiver, true, true, true);

        Whisper.PutLastSender(Receiver.getUniqueID(), ((EntityPlayerMP) sender).getUniqueID());

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "whisper");
    }

}
