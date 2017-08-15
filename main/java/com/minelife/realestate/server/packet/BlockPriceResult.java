package com.minelife.realestate.server.packet;

import com.minelife.realestate.client.Selection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BlockPriceResult implements IMessage {

    private long price;

    public BlockPriceResult() { }

    public BlockPriceResult(long price) {
        this.price = price;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.price = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.price);
    }

    public static class Handler implements IMessageHandler<BlockPriceResult, IMessage> {

        @Override
        public IMessage onMessage(BlockPriceResult message, MessageContext ctx) {
            Selection.setPricePerBlock(message.price);
            return null;
        }

    }

}