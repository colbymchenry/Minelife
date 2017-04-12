package com.minelife.gun;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketKeyStroke implements IMessage {

    private byte key;

    public PacketKeyStroke() {
    }

    public PacketKeyStroke(byte key) {
        this.key = key;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.key = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.key);
    }

    public static class Handler implements IMessageHandler<PacketKeyStroke, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketKeyStroke message, MessageContext ctx) {
            return null;
        }

    }

}
