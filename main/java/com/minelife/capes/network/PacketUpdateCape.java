package com.minelife.capes.network;

import com.minelife.util.client.render.CapeLoader;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketUpdateCape implements IMessage {

    private UUID playerID;

    public PacketUpdateCape() {
    }

    public PacketUpdateCape(UUID playerID) {
        this.playerID = playerID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
    }

    public static class Handler implements IMessageHandler<PacketUpdateCape, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdateCape message, MessageContext ctx) {
            CapeLoader.deleteCape(message.playerID);
            return null;
        }
    }

}
