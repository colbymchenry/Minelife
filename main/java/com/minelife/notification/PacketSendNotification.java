package com.minelife.notification;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class PacketSendNotification implements IMessage {

    private AbstractNotification notification;

    public PacketSendNotification()
    {
    }

    public PacketSendNotification(AbstractNotification notification)
    {
        this.notification = notification;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try {
            Class<? extends AbstractNotification> clazz = (Class<? extends AbstractNotification>) Class.forName(ByteBufUtils.readUTF8String(buf));
            AbstractNotification notification = clazz.newInstance();
            notification.fromBytes(buf);
            this.notification = notification;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, notification.getClass().getName());
        notification.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketSendNotification, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendNotification message, MessageContext ctx)
        {
            try {
                AbstractGuiNotification guiNotification = message.notification.getGuiClass().getConstructor(AbstractNotification.class).newInstance(message.notification);
                message.notification.writeToDB();
                guiNotification.push();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
