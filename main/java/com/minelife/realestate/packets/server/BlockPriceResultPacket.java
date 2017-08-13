package com.minelife.realestate.packets.server;

import com.minelife.realestate.client.estateselection.Selection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BlockPriceResultPacket implements IMessage {

    long price;

    public BlockPriceResultPacket(long price) {
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

    public static class Handler implements IMessageHandler<BlockPriceResultPacket, IMessage> {

        @Override
        public IMessage onMessage(BlockPriceResultPacket packet, MessageContext ctx) {
            Selection.setPricePerBlock(packet.price);
            return null;
        }

    }

    // ---- New Packet System Style ---- //

//    private long price;
//
//    public BlockPriceResultPacket() { }
//
//    public BlockPriceResultPacket(long price) {
//        this.price = price;
//    }
//
//    @Override
//    public Side sideOfHandling() {
//        return Side.CLIENT;
//    }
//
//    @Override
//    public void handle(MessageContext ctx) {
//        Selection.setPricePerBlock(this.price);
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        this.price = buf.readLong();
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        buf.writeLong(this.price);
//    }

}