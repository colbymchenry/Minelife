package com.minelife.essentials.network;

import com.minelife.essentials.client.OnScreenRenderer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketTitleMsg implements IMessage {

    private String title;
    private String subTitle;
    private int duration;

    public PacketTitleMsg(String title, String subTitle, int duration) {
        this.title = title;
        this.subTitle = subTitle;
        this.duration = duration;
    }

    public PacketTitleMsg() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        title = ByteBufUtils.readUTF8String(buf);
        subTitle = ByteBufUtils.readUTF8String(buf);
        duration = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, title);
        ByteBufUtils.writeUTF8String(buf, subTitle);
        buf.writeInt(duration);
    }

    public static class Handler implements IMessageHandler<PacketTitleMsg, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketTitleMsg message, MessageContext ctx) {
            OnScreenRenderer.title = message.title.equals(" ") ? null : message.title;
            OnScreenRenderer.subTitle =  message.subTitle.equals(" ") ? null : message.subTitle;
            OnScreenRenderer.endTime = System.currentTimeMillis() + (message.duration * 1000L);
            return null;
        }
    }

}
