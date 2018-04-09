package com.minelife.realestate.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketRemoveMember implements IMessage {

    private UUID memberID, estateID;

    public PacketRemoveMember() {
    }

    public PacketRemoveMember(UUID memberID, UUID estateID) {
        this.memberID = memberID;
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        memberID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        estateID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberID.toString());
        ByteBufUtils.writeUTF8String(buf, estateID.toString());
    }

    public static class Handler implements IMessageHandler<PacketRemoveMember, IMessage> {

        // TODO
        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRemoveMember message, MessageContext ctx) {
            return null;
        }
    }

}
