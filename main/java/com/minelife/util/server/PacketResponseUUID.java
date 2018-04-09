package com.minelife.util.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketResponseUUID implements IMessage {

    private UUID playerUUID;
    private String playerName;

    public PacketResponseUUID() {
    }

    public PacketResponseUUID(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean())
            playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        if (buf.readBoolean())
            playerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(playerUUID != null);
        if (playerUUID != null)
            ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
        buf.writeBoolean(playerName != null);
        if (playerName != null)
            ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static class Handler implements IMessageHandler<PacketResponseUUID, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketResponseUUID message, MessageContext ctx) {
            UUIDFetcher.CACHE.put(message.playerUUID, message.playerName);
            return null;
        }
    }

}
