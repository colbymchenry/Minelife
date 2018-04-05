package com.minelife.notifications;

import com.minelife.notifications.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketNotification implements IMessage {

    private Notification notification;

    public PacketNotification() {
    }

    public PacketNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        notification = Notification.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        notification.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketNotification, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketNotification message, MessageContext ctx) {
            ClientProxy.notifications.add(message.notification);
            return null;
        }
    }

}
