package com.minelife.capes.server;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.capes.ItemCape;
import com.minelife.capes.network.*;
import com.minelife.util.client.netty.NettyOutbound;
import com.minelife.util.client.render.CapeLoader;
import com.minelife.util.server.MLCommand;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

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
                // check if holding a cape
                if (held_item == null || held_item.getItem() != MLItems.cape) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You must have the cape in hand."));
                    break;
                }

                // check if the cape has pixels
                if(MLItems.cape.getPixels(held_item) == null) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "This is a blank cape. Type '/cape create' to create a design."));
                    break;
                }

                // drop current cape if they have one
                ItemStack to_give = new ItemStack(MLItems.cape);
                if(MLItems.cape.getPixels(player) != null && player.getEntityData().hasKey("cape") && player.getEntityData().getBoolean("cape")) {
                    MLItems.cape.setUUID(to_give);
                    MLItems.cape.setPixels(to_give, MLItems.cape.getPixels(player));
                    EntityItem entity_item = player.dropPlayerItemWithRandomChoice(to_give, false);
                    entity_item.delayBeforeCanPickup = 0;
                }

                // update the netty server
                NettyOutbound outbound = new NettyOutbound(0);
                outbound.write(player.getUniqueID().toString());
                outbound.write(MLItems.cape.getPixels(held_item));
                outbound.send();
                player.getEntityData().setBoolean("cape", true);
                player.writeEntityToNBT(player.getEntityData());

                // set the players data for the new cape
                if(MLItems.cape.getPixels(held_item) != null) MLItems.cape.setPixels(player, MLItems.cape.getPixels(held_item));

                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You have put on your new cape."));
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);

                // update all players with new cape
                Minelife.NETWORK.sendToAll(new PacketUpdateCape(player.getUniqueID()));
                Minelife.NETWORK.sendToAll(new PacketUpdateCapeStatus(player.getEntityId(), true));
                break;
            }
            case "off": {
                // drop current cape if they have one
                ItemStack to_give = new ItemStack(MLItems.cape);
                if(MLItems.cape.getPixels(player) != null && player.getEntityData().hasKey("cape") && player.getEntityData().getBoolean("cape")) {
                    MLItems.cape.setUUID(to_give);
                    MLItems.cape.setPixels(to_give, MLItems.cape.getPixels(player));
                    EntityItem entity_item = player.dropPlayerItemWithRandomChoice(to_give, false);
                    entity_item.delayBeforeCanPickup = 0;
                }

                // set player data to not wear cape
                player.getEntityData().setBoolean("cape", false);
                player.writeEntityToNBT(player.getEntityData());

                // remove cape from player data
                MLItems.cape.setPixels(player, null);
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your cape magically fades away."));
                Minelife.NETWORK.sendToAll(new PacketUpdateCapeStatus(player.getEntityId(), false));

                // packet used to delete cape
                NettyOutbound outbound = new NettyOutbound(1);
                outbound.write(player.getUniqueID().toString());
                outbound.send();
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
