package com.minelife.util.client;

import com.minelife.Minelife;
import com.minelife.util.server.*;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;

import java.util.UUID;

public class PacketRequestName implements IMessage {

    public UUID playerUUID;
    public String receiver;

    public PacketRequestName() {
    }

    public PacketRequestName(UUID playerUUID, String receiver) {
        this.playerUUID = playerUUID;
        this.receiver = receiver;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        receiver = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
        ByteBufUtils.writeUTF8String(buf, receiver);
    }

    public static class Handler implements IMessageHandler<PacketRequestName, IMessage>, Callback {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestName message, MessageContext ctx) {
            FetchNameThread.instance.fetchName(message, ctx, this);
            return null;
        }

        @Override
        public void callback(Object... objects) {
            String name = (String) objects[0];
            PacketRequestName message = (PacketRequestName) objects[1];
            MessageContext ctx = (MessageContext) objects[2];
            Minelife.NETWORK.sendTo(new PacketResponseName(message.playerUUID, name, message.receiver), ctx.getServerHandler().playerEntity);
        }
    }


}
