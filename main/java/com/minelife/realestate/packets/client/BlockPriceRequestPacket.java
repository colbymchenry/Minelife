package com.minelife.realestate.packets.client;

import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.packets.server.BlockPriceResultPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BlockPriceRequestPacket implements IMessage {

    public BlockPriceRequestPacket() { }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<BlockPriceRequestPacket, IMessage> {

        @Override
        public IMessage onMessage(BlockPriceRequestPacket packet, MessageContext ctx) {
            Minelife.NETWORK.sendTo(new BlockPriceResultPacket(ModRealEstate.config.getLong("price_per_block")), ctx.getServerHandler().playerEntity);
            return null;
        }

    }

    // ---- New Packet System Style ---- //

//    public BlockPriceRequestPacket() { }
//
//    @Override
//    public Side sideOfHandling() {
//        return Side.SERVER;
//    }
//
//    @Override
//    public void handle(MessageContext ctx) {
//        Minelife.NETWORK.sendTo(new BlockPriceResultPacket(ModRealEstate.config.getLong("price_per_block")), ctx.getServerHandler().playerEntity);
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//
//    }

}