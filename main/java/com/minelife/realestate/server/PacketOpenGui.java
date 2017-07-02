package com.minelife.realestate.server;

import com.minelife.realestate.client.gui.GuiMenu;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketOpenGui implements IMessage {

    public PacketOpenGui()
    {
    }

    private int pricePerChunk;

    public PacketOpenGui(int pricePerChunk)
    {
        this.pricePerChunk = pricePerChunk;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.pricePerChunk = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.pricePerChunk);
    }

    public static class Handler implements IMessageHandler<PacketOpenGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenGui message, MessageContext ctx)
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiMenu(message.pricePerChunk));
            return null;
        }
    }

}
