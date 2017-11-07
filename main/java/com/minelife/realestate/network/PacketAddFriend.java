package com.minelife.realestate.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketAddFriend implements IMessage {

    private String playerName;
    private int estateID;

    public PacketAddFriend() {
    }

    public PacketAddFriend(String playerName, int estateID) {
        this.playerName = playerName;
        this.estateID = estateID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerName = ByteBufUtils.readUTF8String(buf);
        estateID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
        buf.writeInt(estateID);
    }

    public static class Handler implements IMessageHandler<PacketAddFriend, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketAddFriend message, MessageContext ctx) {
            //
            return null;
        }
    }

}
