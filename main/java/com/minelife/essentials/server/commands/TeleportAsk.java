package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeleportAsk implements ICommand {

    private static Map<EntityPlayerMP, EntityPlayerMP> RequestMap = Maps.newHashMap();

    @Override
    public String getCommandName() {
        return "tpa";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tpa <player>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        EntityPlayerMP Sender = (EntityPlayerMP) commandSender;

        if(args.length == 0) {
            Sender.addChatMessage(new ChatComponentText(getCommandUsage(Sender)));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(args[0]);

        if(Receiver == null) {
            Sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found."));
            return;
        }

        SubmitRequest(Sender, Receiver);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpa");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public static void SubmitRequest(EntityPlayerMP Sender, EntityPlayerMP Receiver) {
        Receiver.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                "Player " + EnumChatFormatting.BLUE + Sender.getCommandSenderName() + EnumChatFormatting.GREEN +
                " has requested to teleport to you. Type " + EnumChatFormatting.BLUE + "/tpaccept" + EnumChatFormatting.GREEN + " to accept."));

        Sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Teleport request sent to " + EnumChatFormatting.BLUE + Receiver.getCommandSenderName()));

        RequestMap.put(Receiver, Sender);
    }

    public static EntityPlayerMP GetRequest(EntityPlayerMP Player) {
        return RequestMap.get(Player);
    }

    public static void DeleteRequest(EntityPlayerMP Player) {
        RequestMap.remove(Player);
    }
}
