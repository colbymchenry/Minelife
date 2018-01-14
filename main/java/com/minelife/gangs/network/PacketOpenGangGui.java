package com.minelife.gangs.network;

import com.minelife.gangs.Gang;
import com.minelife.gangs.client.gui.GuiGangMenu;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketOpenGangGui implements IMessage {

    public PacketOpenGangGui() {
    }

    private Gang gang;

    public PacketOpenGangGui(Gang gang) {
        this.gang = gang;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gang = Gang.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.gang.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketOpenGangGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenGangGui message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiGangMenu(message.gang));
            return null;
        }
    }

}
