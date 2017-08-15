package com.minelife.minebay.packet;

import com.minelife.Minelife;
import com.minelife.minebay.client.gui.PopupGui;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketPopupMsg implements IMessage {

    private String message;

    public PacketPopupMsg()
    {
    }

    public PacketPopupMsg(String message)
    {
        this.message = message;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, message);
    }

    public static class Handler implements IMessageHandler<PacketPopupMsg, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketPopupMsg message, MessageContext ctx)
        {
            Minecraft.getMinecraft().displayGuiScreen(new PopupGui(message.message, Minecraft.getMinecraft().currentScreen));
            return null;
        }
    }

    @SideOnly(Side.SERVER)
    public static void send(String message, EntityPlayerMP player) {
        Minelife.NETWORK.sendTo(new PacketPopupMsg(message), player);
    }

}
