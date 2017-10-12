package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.realestate.network.PacketGuiCreateEstate;
import com.minelife.realestate.network.PacketGuiModifyEstate;
import com.minelife.realestate.network.PacketGuiPurchaseEstate;
import com.minelife.util.PlayerHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.util.Arrays;
import java.util.List;

public class CommandEstate implements ICommand {

    @Override
    public String getCommandName() {
        return "estate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("e");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP player = (EntityPlayerMP) sender;
        try {
            if (args.length == 0) throw new Exception(getCommandUsage(sender));

            Estate estateAtLoc = EstateHandler.getEstateAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

            switch(args[0].toLowerCase()) {
                case "create": {
                    if(EstateHandler.canCreateEstate(player, SelectionHandler.getSelection(player))) {
                        List<Permission> permissions = Lists.newArrayList();
                        if(estateAtLoc == null)
                            permissions.addAll(Arrays.asList(Permission.values()));
                        else {
                            List<Permission> perms = Lists.newArrayList();
                            perms.addAll(estateAtLoc.getPlayerPermissions(player));
                            if(!PlayerHelper.isOp(player)) perms.removeAll(Permission.getEstatePermissions());
                            permissions.addAll(estateAtLoc.getPlayerPermissions(player));
                        }
                        Minelife.NETWORK.sendTo(new PacketGuiCreateEstate(permissions), player);
                    }
                    break;
                }
                case "purchase": {
                    if(estateAtLoc == null) {
                        player.addChatComponentMessage(new ChatComponentText("There is no estate at your location."));
                        return;
                    }

                    if(!estateAtLoc.isPurchasable() && !estateAtLoc.isForRent()) {
                        player.addChatComponentMessage(new ChatComponentText("This estate is not for sale."));
                        return;
                    }

                    Minelife.NETWORK.sendTo(new PacketGuiPurchaseEstate(estateAtLoc), player);
                    break;
                }
                case "modify": {
                    if(estateAtLoc == null) {
                        player.addChatComponentMessage(new ChatComponentText("There is no estate at your location."));
                        return;
                    }

                    Minelife.NETWORK.sendTo(new PacketGuiModifyEstate(estateAtLoc, estateAtLoc.getPlayerPermissions(player)), player);
                    // TODO: Don't allow to open unless they are either A the owner B the renter or C the owner or renter of the land above
                    break;
                }
                default: throw new Exception(getCommandUsage(sender));
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
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
