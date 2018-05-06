package com.minelife.emt;

import com.minelife.Minelife;
import com.minelife.util.PlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketRequestEMTStatus implements IMessage {

    private UUID playerID;

    public PacketRequestEMTStatus() {
    }

    public PacketRequestEMTStatus(UUID playerID) {
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

    public static class Handler implements IMessageHandler<PacketRequestEMTStatus, IMessage> {

        @Override
        public IMessage onMessage(PacketRequestEMTStatus message, MessageContext ctx) {
            boolean isEMT = ModEMT.isEMT(message.playerID);
            Minelife.getNetwork().sendTo(new PacketSendEMTStatus(message.playerID, isEMT), ctx.getServerHandler().player);
            return null;
        }
    }

}
