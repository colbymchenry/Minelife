package com.minelife.gangs.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketChallengeGang implements IMessage  {

    private String gang;

    public PacketChallengeGang() {
    }

    public PacketChallengeGang(String gang) {
        this.gang = gang;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gang = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gang);
    }

    public static class Handler implements IMessageHandler<PacketChallengeGang, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketChallengeGang message, MessageContext ctx) {
            return null;
        }
    }

}
