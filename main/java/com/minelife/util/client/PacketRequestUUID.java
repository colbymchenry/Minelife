package com.minelife.util.client;

import com.minelife.Minelife;
import com.minelife.util.server.NameUUIDCallback;
import com.minelife.util.server.PacketResponseUUID;
import com.minelife.util.server.UUIDFetcher;
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

    public static class Handler implements IMessageHandler<PacketRequestUUID, IMessage>, NameUUIDCallback {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestUUID message, MessageContext ctx) {
            UUIDFetcher.get(message.playerName, this, message, ctx);
            return null;
        }

        @Override
        public void callback(UUID id, String name, Object... objects) {
            PacketRequestUUID message = (PacketRequestUUID) objects[0];
            MessageContext ctx = (MessageContext) objects[1];
            Minelife.NETWORK.sendTo(new PacketResponseUUID(id, name, message.receiver), ctx.getServerHandler().playerEntity);
        }
    }


}
