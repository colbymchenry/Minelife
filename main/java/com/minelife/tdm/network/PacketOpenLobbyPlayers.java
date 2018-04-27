package com.minelife.tdm.network;

import com.minelife.tdm.client.gui.GuiLobby;
import com.minelife.tdm.client.gui.GuiLobbyPlayers;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenLobbyPlayers implements IMessage {

    public PacketOpenLobbyPlayers() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketOpenLobbyPlayers, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenLobbyPlayers message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if(Minecraft.getMinecraft().currentScreen instanceof GuiLobby) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiLobbyPlayers((GuiLobby) Minecraft.getMinecraft().currentScreen));
                }
            });
            return null;
        }
    }

}
