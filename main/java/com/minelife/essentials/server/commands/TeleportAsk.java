package com.minelife.essentials.server.commands;

import com.google.common.collect.Maps;
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
import java.util.Map;

public class TeleportAsk extends CommandBase {

    private static Map<EntityPlayerMP, EntityPlayerMP> RequestMap = Maps.newHashMap();

    @Override
    public String getName() {
        return "tpa";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tpa <player>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) {
        EntityPlayerMP Sender = (EntityPlayerMP) commandSender;

        if(args.length == 0) {
            Sender.sendMessage(new TextComponentString(getUsage(Sender)));
            return;
        }

        EntityPlayerMP Receiver = PlayerHelper.getPlayer(args[0]);

        if(Receiver == null) {
            Sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
            return;
        }

        SubmitRequest(Sender, Receiver);
        Receiver.sendMessage(new TextComponentString(TextFormatting.GREEN +
                "Player " + TextFormatting.BLUE + Sender.getName() + TextFormatting.GREEN +
                " has requested to teleport to you. Type " + TextFormatting.BLUE + "/tpaccept" + TextFormatting.GREEN + " to accept."));

        Receiver.getEntityData().removeTag("tpahere");

        Sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleport request sent to " + TextFormatting.BLUE + Receiver.getName()));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpa");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return isUsernameIndex(args, args.length) ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : null;
    }

    public static void SubmitRequest(EntityPlayerMP Sender, EntityPlayerMP Receiver) {
        RequestMap.put(Receiver, Sender);
    }

    public static EntityPlayerMP GetRequest(EntityPlayerMP Player) {
        return RequestMap.get(Player);
    }

    public static void DeleteRequest(EntityPlayerMP Player) {
        RequestMap.remove(Player);
    }
}
