package com.minelife.police.network;

import com.minelife.Minelife;
import com.minelife.police.GuiHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketOpenTicketInventory implements IMessage {

    private int ticketSlot;

    public PacketOpenTicketInventory(int ticketSlot) {
        this.ticketSlot = ticketSlot;
    }

    public PacketOpenTicketInventory() {
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        ticketSlot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(ticketSlot);
    }

    public static class Handler implements IMessageHandler<PacketOpenTicketInventory, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenTicketInventory message, MessageContext ctx) {
            ctx.getServerHandler().playerEntity.openGui(Minelife.MOD_ID, GuiHandler.ticketInventoryID, ctx.getServerHandler().playerEntity.worldObj, message.ticketSlot, 0, 0);
            return null;
        }
    }

}
