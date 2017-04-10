package com.minelife.economy.packet;

import com.minelife.economy.client.gui.GuiSetPin;
import com.minelife.economy.client.gui.GuiUnlock;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketOpenATM implements IMessage {

    public PacketOpenATM() {
    }

    private boolean setPin = false;

    public PacketOpenATM(boolean setPin) {
        this.setPin = setPin;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.setPin = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.setPin);
    }

    public static class Handler implements IMessageHandler<PacketOpenATM, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenATM message, MessageContext ctx) {
            if (message.setPin)
                Minecraft.getMinecraft().displayGuiScreen(new GuiSetPin(false));
            else
                Minecraft.getMinecraft().displayGuiScreen(new GuiUnlock());

            return null;
        }

    }

}
