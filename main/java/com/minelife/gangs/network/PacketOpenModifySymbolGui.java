package com.minelife.gangs.network;

import com.minelife.gangs.client.GuiModifyCape;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketOpenModifySymbolGui implements IMessage {

    public PacketOpenModifySymbolGui() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketOpenModifySymbolGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenModifySymbolGui message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiModifyCape());
            return null;
        }
    }

}
