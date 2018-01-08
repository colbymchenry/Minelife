package com.minelife.util.client.netty;

import com.minelife.Minelife;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketSendNettyServer implements IMessage {

    private String ip;
    private int port;

    public PacketSendNettyServer() {

    }

    public PacketSendNettyServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ip = ByteBufUtils.readUTF8String(buf);
        port = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, ip);
        buf.writeInt(port);
    }

    public static class Handler implements IMessageHandler<PacketSendNettyServer, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendNettyServer message, MessageContext ctx) {
            try {
                if(Minelife.NETTY_CONNECTION != null) Minelife.NETTY_CONNECTION.shutdown();
                Minelife.NETTY_CONNECTION = new ChatClient(message.ip, message.port);
                Minelife.NETTY_CONNECTION.run();
            }catch(Exception e) {

            }
            return null;
        }
    }

}
