package com.minelife.essentials.server.commands;

import com.minelife.essentials.Location;
import com.minelife.essentials.TeleportHandler;
import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TeleportAccept extends CommandBase {

    @Override
    public String getName() {
        return "tpaccept";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/tpaccept";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        EntityPlayerMP Player = (EntityPlayerMP) sender;

        EntityPlayerMP Sender = TeleportAsk.GetRequest(Player);

        if(Sender == null) {
            Player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have any teleportation requests."));
            return;
        }

        if(Player.getEntityData().hasKey("tpahere")) {
            Player.getEntityData().removeTag("tpahere");
            Sender.getEntityData().removeTag("tpahere");
            Player = Sender;
            Sender = (EntityPlayerMP) sender;
        }

        Location PlayerLocation = new Location(Player.getEntityWorld().provider.getDimension(), Player.posX, Player.posY, Player.posZ, Player.rotationYaw, Player.rotationPitch);

        TeleportHandler.teleport(Sender, PlayerLocation);
        TeleportAsk.DeleteRequest(Player);
        TeleportAsk.DeleteRequest(Sender);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "tpaccept");
    }

}
