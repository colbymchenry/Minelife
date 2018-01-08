package com.minelife.capes.server;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.capes.network.*;
import com.minelife.util.client.netty.NettyOutbound;
import com.minelife.util.server.MLCommand;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandCape implements ICommand {
    @Override
    public String getCommandName() {
        return "cape";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("/cape on"));
        sender.addChatMessage(new ChatComponentText("/cape off"));
        sender.addChatMessage(new ChatComponentText("/cape edit"));
        sender.addChatMessage(new ChatComponentText("/cape create"));
        sender.addChatMessage(new ChatComponentText("/cape wear"));
        return null;
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            getCommandUsage(sender);
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;
        ItemStack held_item = player.getHeldItem();

        switch (args[0].toLowerCase()) {
            case "on": {
                if (held_item == null || held_item.getItem() != MLItems.cape) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You must have the cape in hand."));
                    break;
                }

                player.getEntityData().setBoolean("cape", true);
                player.writeEntityToNBT(player.getEntityData());
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your cape magically appears."));
                Minelife.NETWORK.sendToAll(new PacketUpdateCapeStatus(player.getEntityId(), true));
                break;
            }
            case "off": {
                player.getEntityData().setBoolean("cape", false);
                player.writeEntityToNBT(player.getEntityData());
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your cape magically fades away."));
                Minelife.NETWORK.sendToAll(new PacketUpdateCapeStatus(player.getEntityId(), false));
                break;
            }
            case "edit": {
                if (held_item == null || held_item.getItem() != MLItems.cape) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You must have the cape in hand."));
                    break;
                }

                if(MLItems.cape.getPixels(held_item) == null) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "This is a blank cape. Type '/cape create' to create a design."));
                    break;
                }

                Minelife.NETWORK.sendTo(new PacketEditGui(), player);
                break;
            }
            case "create": {
                if (held_item == null || held_item.getItem() != MLItems.cape) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You must have the cape in hand."));
                    break;
                }

                if(MLItems.cape.getPixels(held_item) != null) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "This cape already has a design. Type '/cape edit' to edit the design."));
                    break;
                }

                Minelife.NETWORK.sendTo(new PacketCreateGui(), player);
                break;
            }
            case "wear": {
                if (held_item == null || held_item.getItem() != MLItems.cape) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You must have the cape in hand."));
                    break;
                }

                if(MLItems.cape.getPixels(held_item) == null) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "This is a blank cape. Type '/cape create' to create a design."));
                    break;
                }

                NettyOutbound outbound = new NettyOutbound(0);
                outbound.write(player.getUniqueID().toString());
                outbound.write(MLItems.cape.getPixels(held_item));
                outbound.send();
                player.getEntityData().setBoolean("cape", true);
                player.writeEntityToNBT(player.getEntityData());

                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You have put on your new cape."));

                Minelife.NETWORK.sendToAll(new PacketUpdateCape(player.getUniqueID()));
                Minelife.NETWORK.sendToAll(new PacketUpdateCapeStatus(player.getEntityId(), true));
                break;
            }
            default: {
                getCommandUsage(sender);
                break;
            }
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
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
