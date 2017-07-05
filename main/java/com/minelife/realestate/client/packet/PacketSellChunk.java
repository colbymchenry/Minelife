package com.minelife.realestate.client.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSellChunk implements IMessage {

    public PacketSellChunk()
    {
    }

    private String title, description;
    private long price;
    private boolean forRent, allowPlacement, allowBreaking, allowGuests;

    public PacketSellChunk(String title, String description, long price, boolean forRent, boolean allowPlacement, boolean allowBreaking, boolean allowGuests)
    {
        this.title = title;
        this.description = description;
        this.price = price;
        this.forRent = forRent;
        this.allowPlacement = allowPlacement;
        this.allowBreaking = allowBreaking;
        this.allowGuests = allowGuests;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.title = ByteBufUtils.readUTF8String(buf);
        this.description = ByteBufUtils.readUTF8String(buf);
        this.price = buf.readLong();
        this.forRent = buf.readBoolean();
        this.allowPlacement = buf.readBoolean();
        this.allowBreaking = buf.readBoolean();
        this.allowGuests = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, title);
        ByteBufUtils.writeUTF8String(buf, description);
        buf.writeLong(price);
        buf.writeBoolean(forRent);
        buf.writeBoolean(allowPlacement);
        buf.writeBoolean(allowBreaking);
        buf.writeBoolean(allowGuests);
    }

    public static class Handler implements IMessageHandler<IMessage, PacketSellChunk> {

        @Override
        public PacketSellChunk onMessage(IMessage message, MessageContext ctx)
        {
            //TODO
            return null;
        }
    }
}
