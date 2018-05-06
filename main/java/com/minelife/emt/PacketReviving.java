package com.minelife.emt;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReviving implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketReviving, IMessage> {

        @Override
        public IMessage onMessage(PacketReviving message, MessageContext ctx) {
            ClientProxy.timeToHeal = System.currentTimeMillis() + (1000L * 21);
            return null;
        }
    }

}
