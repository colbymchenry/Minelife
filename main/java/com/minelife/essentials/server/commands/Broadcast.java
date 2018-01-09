package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.StringHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class Broadcast implements ICommand {

    @Override
    public String getCommandName() {
        return "broadcast";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/broadcast <message>";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String msg = "";

        for (String arg : args) msg += arg;

        MinecraftServer.getServer().addChatMessage(new ChatComponentText(StringHelper.ParseFormatting(msg, '&')));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return !(sender instanceof EntityPlayer) ? true : ModPermission.hasPermission(((EntityPlayer) sender).getUniqueID(), "broadcast");
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
    public int compareTo(Object o) {
        return 0;
    }
}
