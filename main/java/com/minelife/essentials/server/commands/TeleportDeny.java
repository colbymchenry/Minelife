package com.minelife.essentials.server.commands;

import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TeleportDeny extends CommandBase {

    @Override
    public String getName() {
        return "tpdeny";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tpdeny";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        EntityPlayerMP Receiver = TeleportAsk.GetRequest(Player);

        if(Receiver == null) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "You have no teleport requests."));
            return;
        }

        Receiver.sendMessage(new TextComponentString(TextFormatting.RED + "Your request was denied."));
        Player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleport request denied."));

        Receiver.getEntityData().removeTag("tpahere");
        Player.getEntityData().removeTag("tpahere");
        TeleportAsk.DeleteRequest(Player);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpdeny");
    }

}
