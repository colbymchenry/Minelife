package com.minelife.util.server;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class PacketUpdatePlayerInventory implements IMessage {

    private Map<Integer, ItemStack> item_stack;
    private EntityPlayer player;

    public PacketUpdatePlayerInventory() {}

    public PacketUpdatePlayerInventory(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        item_stack = Maps.newHashMap();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            if(buf.readBoolean()) {
                item_stack.put(i, ByteBufUtils.readItemStack(buf));
            } else {
                item_stack.put(i, null);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(player.inventory.getSizeInventory());
        for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
            buf.writeBoolean(player.inventory.getStackInSlot(i) != null);
            if(player.inventory.getStackInSlot(i) != null) {
                ByteBufUtils.writeItemStack(buf, player.inventory.getStackInSlot(i));
            }
        }
    }

    public static class Handler implements IMessageHandler<PacketUpdatePlayerInventory, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdatePlayerInventory message, MessageContext ctx)
        {
            for (Integer slot : message.item_stack.keySet()) {
                System.out.println(message.item_stack.get(slot) == null);
                Minecraft.getMinecraft().thePlayer.inventory.setInventorySlotContents(slot, message.item_stack.get(slot));
            }
            Minecraft.getMinecraft().thePlayer.inventoryContainer.detectAndSendChanges();
            return null;
        }
    }
}
