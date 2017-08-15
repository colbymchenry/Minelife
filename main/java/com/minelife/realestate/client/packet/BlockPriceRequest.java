package com.minelife.realestate.client.packet;

import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.server.packet.BlockPriceResult;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class BlockPriceRequest implements IMessage {

    public BlockPriceRequest() { }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<BlockPriceRequest, BlockPriceResult> {

        @Override
        public BlockPriceResult onMessage(BlockPriceRequest message, MessageContext ctx) {
            return new BlockPriceResult(ModRealEstate.config.getLong("price_per_block", 2));
        }

    }

}