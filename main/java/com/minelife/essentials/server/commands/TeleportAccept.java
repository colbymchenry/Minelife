package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.minelife.essentials.TeleportHandler;
import com.minelife.permission.ModPermission;
import com.minelife.util.Location;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class TeleportAccept implements ICommand {

    @Override
    public String getCommandName() {
        return "tpaccept";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tpaccept";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        EntityPlayerMP Sender = TeleportAsk.GetRequest(Player);

        if(Sender == null) {
            Player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have any teleportation requests."));
            return;
        }

        if(Player.getEntityData().hasKey("tpahere")) {
            Player.getEntityData().removeTag("tpahere");
            Sender.getEntityData().removeTag("tpahere");
            Player = Sender;
            Sender = (EntityPlayerMP) sender;
        }

        Location PlayerLocation = new Location(Player.worldObj.getWorldInfo().getWorldName(), Player.posX, Player.posY, Player.posZ, Player.rotationYaw, Player.rotationPitch);

        TeleportHandler.teleport(Sender, PlayerLocation);
        TeleportAsk.DeleteRequest(Player);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpaccept");
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
