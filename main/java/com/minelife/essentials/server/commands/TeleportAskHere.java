package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;

public class TeleportAskHere extends CommandBase {

    @Override
    public String getName() {
        return "tpahere";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tpahere <player>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        if(args.length == 0) {
            Player.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(args[0]);

        if(Receiver == null) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        if(Mute.getMutedPlayers(Receiver).contains(Player.getUniqueID())) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "Player has muted you."));
            return;
        }

        TeleportAsk.SubmitRequest(Player, Receiver);
        Receiver.getEntityData().setBoolean("tpahere", true);
        Receiver.sendMessage(new TextComponentString(TextFormatting.GREEN +
                "Player " + TextFormatting.BLUE + Player.getName() + TextFormatting.GREEN +
                " has requested for you to teleport to them. Type " + TextFormatting.BLUE + "/tpaccept" + TextFormatting.GREEN + " to accept."));

        Player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleport request sent to " + TextFormatting.BLUE + Receiver.getName()));

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpahere");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return isUsernameIndex(args, args.length) ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Lists.newArrayList();
    }

}
