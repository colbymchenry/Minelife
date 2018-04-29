package com.minelife.airdrop;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.ListIterator;

public class PacketAirdrop implements IMessage {

    public PacketAirdrop() {
    }

    public Airdrop airdrop;

    public PacketAirdrop(Airdrop airdrop) {
        this.airdrop = airdrop;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        airdrop = Airdrop.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        airdrop.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketAirdrop, IMessage> {

        @SideOnly(Side.CLIENT)
        public synchronized IMessage onMessage(PacketAirdrop message, MessageContext ctx) {
            ListIterator<Airdrop> airdropIterator = ModAirdrop.airdrops.listIterator();
            while (airdropIterator.hasNext()) {
                Airdrop airdrop = airdropIterator.next();
                if (airdrop.equals(message.airdrop)) {
                    airdrop.y = message.airdrop.y;
                    return null;
                }
            }

            airdropIterator.add(message.airdrop);
            return null;
        }
    }

}
