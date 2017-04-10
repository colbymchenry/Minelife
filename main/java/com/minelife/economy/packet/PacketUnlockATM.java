package com.minelife.economy.packet;

import com.minelife.economy.client.gui.GuiMainMenu;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class PacketUnlockATM implements IMessage {

    public PacketUnlockATM() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketUnlockATM, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUnlockATM message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
            return null;
        }

    }

}
