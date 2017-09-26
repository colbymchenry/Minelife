package com.minelife.police.network;

import com.minelife.Minelife;
import com.minelife.police.GuiHandler;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketOpenTicketInventory implements IMessage {

    private int ticketSlot = -1;
    private ItemStack ticketStack;

    public PacketOpenTicketInventory(int ticketSlot)
    {
        this.ticketSlot = ticketSlot;
    }

    public PacketOpenTicketInventory(ItemStack ticketStack)
    {
        this.ticketStack = ticketStack;
    }

    public PacketOpenTicketInventory()
    {
    }


    @Override
    public void fromBytes(ByteBuf buf)
    {
        ticketSlot = buf.readInt();
        if (buf.readBoolean()) ticketStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(ticketSlot);
        buf.writeBoolean(ticketStack != null);
        if (ticketStack != null) ByteBufUtils.writeItemStack(buf, ticketStack);
    }

    public static class Handler implements IMessageHandler<PacketOpenTicketInventory, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenTicketInventory message, MessageContext ctx)
        {
            EntityPlayerMP p = ctx.getServerHandler().playerEntity;

            // check if we are from the computer
            if (message.ticketSlot == -1) {
                message.ticketSlot = 0;
                // get the players old stack
                ItemStack oldStack = p.inventory.getStackInSlot(message.ticketSlot);
                if(oldStack != null) {
                    // drop the old stack
                    EntityItem entity_old_stack = p.dropPlayerItemWithRandomChoice(oldStack, false);
                    entity_old_stack.delayBeforeCanPickup = 0;
                }

                // set the stack to the ticket ItemStack
                p.inventory.setInventorySlotContents(message.ticketSlot, message.ticketStack);
                // we put a 1 on the y axis that way we know it is from the computer
                p.openGui(Minelife.MOD_ID, GuiHandler.ticketInventoryID, p.worldObj, message.ticketSlot, 1, 0);
                return null;
            }
            p.openGui(Minelife.MOD_ID, GuiHandler.ticketInventoryID, p.worldObj, message.ticketSlot, 0, 0);
            return null;
        }
    }

}
