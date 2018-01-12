package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class TeleportAskHere implements ICommand {

    @Override
    public String getCommandName() {
        return "tpahere";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tpahere <player>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if(args.length == 0) {
            Player.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(args[0]);

        if(Receiver == null) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Player not found."));
            return;
        }

        TeleportAsk.SubmitRequest(Player, Receiver);
        Receiver.getEntityData().setBoolean("tpahere", true);
        Receiver.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN +
                "Player " + EnumChatFormatting.BLUE + Player.getCommandSenderName() + EnumChatFormatting.GREEN +
                " has requested for you to teleport to them. Type " + EnumChatFormatting.BLUE + "/tpaccept" + EnumChatFormatting.GREEN + " to accept."));

        Player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Teleport request sent to " + EnumChatFormatting.BLUE + Receiver.getCommandSenderName()));

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpahere");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
