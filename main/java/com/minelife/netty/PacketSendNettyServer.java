package com.minelife.netty;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
                if(ModNetty.getNettyConnection() != null) ModNetty.getNettyConnection().shutdown();
                ModNetty.setNettyConnection(new ChatClient(message.ip, message.port));
                ModNetty.getNettyConnection().run();
            }catch(Exception e) {

            }
            return null;
        }
    }

}
