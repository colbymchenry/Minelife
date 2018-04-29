package com.minelife.airdrop;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRemoveAirdrop implements IMessage {

    private Airdrop airdrop;

    public PacketRemoveAirdrop() {
    }

    public PacketRemoveAirdrop(Airdrop airdrop) {
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

    public static class Handler implements IMessageHandler<PacketRemoveAirdrop, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketRemoveAirdrop message, MessageContext ctx) {
            ModAirdrop.airdrops.remove(message.airdrop);
            return null;
        }
    }

}
