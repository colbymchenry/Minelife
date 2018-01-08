package com.minelife.capes.network;

import com.minelife.capes.client.GuiCreateCape;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketCreateGui implements IMessage {

    public PacketCreateGui() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketCreateGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketCreateGui message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiCreateCape());
            return null;
        }
    }

}
