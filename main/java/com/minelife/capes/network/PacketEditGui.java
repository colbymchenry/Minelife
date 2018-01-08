package com.minelife.capes.network;

import com.minelife.capes.client.GuiCreateCape;
import com.minelife.capes.client.GuiEditCape;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketEditGui implements IMessage {

    public PacketEditGui() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketEditGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketEditGui message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEditCape(Minecraft.getMinecraft().thePlayer.getHeldItem()));
            return null;
        }
    }

}
