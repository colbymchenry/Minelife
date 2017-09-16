package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.police.ModPolice;
import com.minelife.region.server.Region;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.util.List;

public class CommandPolice implements ICommand {

    @Override
    public String getCommandName() {
        return "police";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("/police prison set region"));
        sender.addChatMessage(new ChatComponentText("/police prison set exit"));
        sender.addChatMessage(new ChatComponentText("/police prison set enter"));
        sender.addChatMessage(new ChatComponentText("/police prison delete"));
        sender.addChatMessage(new ChatComponentText("/police prison"));
        sender.addChatMessage(new ChatComponentText("/police lockup <player>"));
        sender.addChatMessage(new ChatComponentText("/police free <player>"));
        sender.addChatMessage(new ChatComponentText("/police pardon <player>"));
        return "";
    }

    @Override
    public List getCommandAliases() {
        return Lists.newArrayList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String cmd = args[0];

        EntityPlayerMP player = (EntityPlayerMP) sender;

        switch (cmd) {
            case "prison": {
                prisonCmd(player, args);
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
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

    private void prisonCmd(EntityPlayerMP player, String[] args) {
        if (args.length == 0) {
            teleportToPrison:
            {
                Vec3 tpVec = ModPolice.getServerProxy().getPrisonEntrance();
                if (tpVec == null) {
                    player.addChatComponentMessage(new ChatComponentText("Teleport location not defined."));
                } else {
                    player.mountEntity(null);
                    player.playerNetServerHandler.setPlayerLocation(tpVec.xCoord, tpVec.yCoord, tpVec.zCoord, player.rotationYaw, player.rotationPitch);
                }
            }

            return;
        }

        Region region = Region.getRegionAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

        if(region == null) {
            player.addChatComponentMessage(new ChatComponentText("No region was found at your location."));
            return;
        }

        if (args[1].equalsIgnoreCase("set")) {
            if(args[2].equalsIgnoreCase("region")) {
                ModPolice.getServerProxy().setPrisonRegion(region);
                player.addChatComponentMessage(new ChatComponentText("Prison yard region has been set!"));
            } else if(args[2].equalsIgnoreCase("exit")) {
                ModPolice.getServerProxy().setPrisonExit(player.posX, player.posY, player.posZ);
                player.addChatComponentMessage(new ChatComponentText("Prison yard exit has been set!"));
            } else if(args[2].equalsIgnoreCase("enter")) {
                ModPolice.getServerProxy().setPrisonEntrance(player.posX, player.posY, player.posZ);
                player.addChatComponentMessage(new ChatComponentText("Prison yard entrance has been set!"));
            }
        } else if (args[1].equalsIgnoreCase("delete")) {
            ModPolice.getServerProxy().setPrisonRegion(null);
            player.addChatComponentMessage(new ChatComponentText("Prison yard has been removed!"));
        } else {
            getCommandUsage(player);
        }
    }

    private void lockupCmd(EntityPlayerMP player, String[] args) {
        if(args.length == 0) {
            getCommandUsage(player);
            return;
        }

        // TODO
    }

}
