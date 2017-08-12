package com.minelife.minebay.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketListings implements IMessage {

    public PacketListings() {}

    private int page;

    public PacketListings(int page) {
        this.page = page;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.page = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.page);
    }

    public static class Handler implements IMessageHandler<PacketListings, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketListings message, MessageContext ctx)
        {

            return null;
        }
    }
}
