package com.minelife.gun.packet;

import com.minelife.MLItems;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketSetSite implements IMessage {

    private int id;

    public PacketSetSite() {
    }

    public PacketSetSite(int id) {
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public static class Handler implements IMessageHandler<PacketSetSite, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetSite message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack heldItem = player.getHeldItem();

            if(heldItem == null) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Gun not found."));
                player.closeScreen();
                return null;
            }

            if(!(heldItem.getItem() instanceof ItemGun)) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Gun not found."));
                player.closeScreen();
                return null;
            }

            if(message.id == 0) {
                int slot = findItem(MLItems.holographicSite, player);
                if(slot > -1) {
                    ItemGun.setSite(heldItem, player.inventory.getStackInSlot(slot));
                    player.inventory.setInventorySlotContents(slot, null);
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem);
                } else {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Holographic Site not found."));
                }
            }
            return null;
        }

        public int findItem(Item item, EntityPlayerMP player) {
            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                if(player.inventory.getStackInSlot(i) != null) {
                    if(player.inventory.getStackInSlot(i).getItem() == item) {
                        return i;
                    }
                }
            }

            return -1;
        }
    }



}
