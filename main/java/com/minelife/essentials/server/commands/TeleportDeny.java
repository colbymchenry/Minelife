package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class TeleportDeny implements ICommand {

    @Override
    public String getCommandName() {
        return "tpdeny";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tpdeny";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        EntityPlayerMP Receiver = TeleportAsk.GetRequest(Player);

        if(Receiver == null) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You have no teleport requests."));
            return;
        }

        Receiver.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Your request was denied."));
        Player.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Teleport request denied."));

        Receiver.getEntityData().removeTag("tpahere");
        Player.getEntityData().removeTag("tpahere");
        TeleportAsk.DeleteRequest(Player);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpdeny");
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
