package com.minelife.economy.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenATM implements IMessage {

    private int balance;

    public PacketOpenATM() {
    }

    public PacketOpenATM(int balance) {
        this.balance = balance;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.balance = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.balance);
    }

    public static class Handler implements IMessageHandler<PacketOpenATM, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenATM message, MessageContext ctx) {
            return null;
        }
    }

}
