package com.minelife.util.client;

import com.minelife.Minelife;
import com.minelife.util.server.NameUUIDCallback;
import com.minelife.util.server.PacketResponseUUID;
import com.minelife.util.server.UUIDFetcher;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketRequestUUID implements IMessage {

    public String playerName;

    public PacketRequestUUID() {
    }

    public PacketRequestUUID(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static class Handler implements IMessageHandler<PacketRequestUUID, IMessage>, NameUUIDCallback {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestUUID message, MessageContext ctx) {
            UUIDFetcher.get(message.playerName, this, ctx);
            return null;
        }

        @Override
        public void callback(UUID id, String name, Object... objects) {
            MessageContext ctx = (MessageContext) objects[0];
            Minelife.getNetwork().sendTo(new PacketResponseUUID(id, name), ctx.getServerHandler().player);
        }
    }


}
