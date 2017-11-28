package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.permission.ModPermission;
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
import java.util.Objects;

public class CommandEstate implements ICommand {

    @Override
    public String getCommandName() {
        return "estate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/e create\n/e modify\n/e buy|rent|purchase";
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
                    if(!ModPermission.hasPermission(player.getUniqueID(), "estate.create")) {
                        player.addChatComponentMessage(new ChatComponentText("You do not have permission to create estates."));
                        return;
                    }

                    if(EstateHandler.canCreateEstate(player, SelectionHandler.getSelection(player))) {
                        List<Permission> permissions = Lists.newArrayList();
                        if(estateAtLoc == null)
                            permissions.addAll(Arrays.asList(Permission.values()));
                        else {
                            if(!estateAtLoc.getPlayerPermissions(player.getUniqueID()).contains(Permission.ESTATE_CREATION)) {
                                player.addChatComponentMessage(new ChatComponentText("You do not have permission to create an estate here."));
                                return;
                            }
                            List<Permission> perms = Lists.newArrayList();
                            perms.addAll(estateAtLoc.getPlayerPermissions(player.getUniqueID()));
                            if(!PlayerHelper.isOp(player)) perms.removeAll(Permission.getEstatePermissions());
                            permissions.addAll(estateAtLoc.getPlayerPermissions(player.getUniqueID()));
                        }
                        Minelife.NETWORK.sendTo(new PacketGuiCreateEstate(permissions), player);
                    }
                    break;
                }
                case "purchase": {
                    doPurchase(estateAtLoc, player);
                    break;
                }
                case "buy": {
                    doPurchase(estateAtLoc, player);
                    break;
                }
                case "rent": {
                    doPurchase(estateAtLoc, player);
                    break;
                }
                case "modify": {
                    if(estateAtLoc == null) {
                        player.addChatComponentMessage(new ChatComponentText("There is no estate at your location."));
                        return;
                    }

                    if(Objects.equals(estateAtLoc.getOwner(), player.getUniqueID()) || Objects.equals(estateAtLoc.getRenter(), player.getUniqueID()) ||
                            estateAtLoc.isAbsoluteOwner(player.getUniqueID()) || estateAtLoc.getMembers().containsKey(player.getUniqueID())) {
                        Minelife.NETWORK.sendTo(new PacketGuiModifyEstate(estateAtLoc, estateAtLoc.getPlayerPermissions(player.getUniqueID())), player);
                    } else {
                        player.addChatComponentMessage(new ChatComponentText("You do not have permission to modify this estate."));
                    }
                    break;
                }
                default: throw new Exception(getCommandUsage(sender));
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
        }
    }

    private void doPurchase(Estate estateAtLoc, EntityPlayerMP player) {
        if(estateAtLoc == null) {
            player.addChatComponentMessage(new ChatComponentText("There is no estate at your location."));
            return;
        }

        if(!estateAtLoc.isPurchasable() && !estateAtLoc.isForRent()) {
            player.addChatComponentMessage(new ChatComponentText("This estate is not for sale."));
            return;
        }

        Minelife.NETWORK.sendTo(new PacketGuiPurchaseEstate(estateAtLoc), player);
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
