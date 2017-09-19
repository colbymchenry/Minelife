package com.minelife.util.client;

import com.minelife.Minelife;
import com.minelife.util.server.Callback;
import com.minelife.util.server.FetchNameThread;
import com.minelife.util.server.FetchUUIDThread;
import com.minelife.util.server.PacketResponseUUID;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketRequestUUID implements IMessage {

    public String playerName;
    public String receiver;

    public PacketRequestUUID() {
    }

    public PacketRequestUUID(String playerName, String receiver) {
        this.playerName = playerName;
        this.receiver = receiver;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerName = ByteBufUtils.readUTF8String(buf);
        receiver = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
        ByteBufUtils.writeUTF8String(buf, receiver);
    }

    public static class Handler implements IMessageHandler<PacketRequestUUID, IMessage>, Callback {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestUUID message, MessageContext ctx) {
            FetchUUIDThread.instance.fetchUUID(message, ctx, this);
            return null;
        }

        @Override
        public void callback(Object... objects) {
            UUID playerUUID = (UUID) objects[0];
            String playerName = (String) objects[1];
            PacketRequestUUID message = (PacketRequestUUID) objects[2];
            MessageContext ctx = (MessageContext) objects[3];
            Minelife.NETWORK.sendTo(new PacketResponseUUID(playerUUID, playerName, message.receiver), ctx.getServerHandler().playerEntity);
        }
    }


}
