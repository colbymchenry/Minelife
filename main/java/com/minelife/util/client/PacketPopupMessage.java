package com.minelife.util.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class PacketPopupMessage implements IMessage {

    private String message;
    private int color;

    public PacketPopupMessage() {}

    public PacketPopupMessage(String message, int color) {
        this.message = message;
        this.color = color;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        message = ByteBufUtils.readUTF8String(buf);
        color = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, message);
        buf.writeInt(color);
    }

    public static class Handler implements IMessageHandler<PacketPopupMessage, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketPopupMessage message, MessageContext ctx) {
            GuiScreen previousScreen = Minecraft.getMinecraft().currentScreen;
            Minecraft.getMinecraft().displayGuiScreen(new GuiPopup(message.message, message.color, previousScreen));
            return null;
        }
    }

}
