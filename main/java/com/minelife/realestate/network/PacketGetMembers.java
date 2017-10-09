package com.minelife.realestate.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketGetMembers implements IMessage {

    private int estateID;

    public PacketGetMembers() {
    }

    public PacketGetMembers(int estateID) {
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estateID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(estateID);
    }

    public static class Handler implements IMessageHandler<PacketGetMembers, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketGetMembers message, MessageContext ctx) {
            return null;
        }
    }

}
