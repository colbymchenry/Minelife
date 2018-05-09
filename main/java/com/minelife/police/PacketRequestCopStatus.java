package com.minelife.police;

import com.minelife.Minelife;
import com.minelife.emt.ModEMT;
import com.minelife.emt.PacketRequestEMTStatus;
import com.minelife.emt.PacketSendEMTStatus;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketRequestCopStatus implements IMessage {

    private UUID playerID;

    public PacketRequestCopStatus() {
    }

    public PacketRequestCopStatus(UUID playerID) {
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

    public static class Handler implements IMessageHandler<PacketRequestCopStatus, IMessage> {

        @Override
        public IMessage onMessage(PacketRequestCopStatus message, MessageContext ctx) {
            boolean isCop = ModPolice.isCop(message.playerID);
            Minelife.getNetwork().sendTo(new PacketSendCopStatus(message.playerID, isCop), ctx.getServerHandler().player);
            return null;
        }
    }

}
