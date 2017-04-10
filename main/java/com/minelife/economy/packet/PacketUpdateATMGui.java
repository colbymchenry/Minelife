package com.minelife.economy.packet;

import com.minelife.economy.client.gui.GuiATM;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketUpdateATMGui implements IMessage {

    private String message;

    public PacketUpdateATMGui() {
    }

    public PacketUpdateATMGui(String message) {
        this.message = message;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.message = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.message);
    }

    public static class Handler implements IMessageHandler<PacketUpdateATMGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdateATMGui message, MessageContext ctx) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiATM) {
                GuiATM guiATM = (GuiATM) Minecraft.getMinecraft().currentScreen;
                guiATM.setStatusMessage(message.message);
            }
            return null;
        }
    }

}
