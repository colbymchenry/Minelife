package com.minelife.notifications;

import com.minelife.notifications.client.OverlayRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.SQLException;

public class PacketNotification implements IMessage {

    private Notification notification;
    private boolean playSound, render, save;

    public PacketNotification() {
    }

    public PacketNotification(Notification notification, boolean playSound, boolean render, boolean save) {
        this.notification = notification;
        this.playSound = playSound;
        this.render = render;
        this.save = save;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        notification = Notification.fromBytes(buf);
        playSound = buf.readBoolean();
        render = buf.readBoolean();
        save = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        notification.toBytes(buf);
        buf.writeBoolean(playSound);
        buf.writeBoolean(render);
        buf.writeBoolean(save);
    }

    public static class Handler implements IMessageHandler<PacketNotification, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketNotification message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> OverlayRenderer.addNotification(message.notification, message.playSound, message.render));
            if(message.save) {
                try {
                    message.notification.save();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}
