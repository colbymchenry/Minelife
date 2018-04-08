package com.minelife.cape.network;

import com.minelife.util.client.render.CapeLoader;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
