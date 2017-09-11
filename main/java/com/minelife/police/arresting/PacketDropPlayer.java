package com.minelife.police.arresting;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketDropPlayer implements IMessage {

    public PacketDropPlayer() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketDropPlayer, IMessage> {

        @Override
        public IMessage onMessage(PacketDropPlayer message, MessageContext ctx) {
            if (ctx.getServerHandler().playerEntity.riddenByEntity != null)
                ctx.getServerHandler().playerEntity.riddenByEntity.mountEntity(null);
            return null;
        }
    }
}
