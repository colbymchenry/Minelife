package com.minelife.tdm.network;

import com.minelife.tdm.client.gui.GuiLobbyPlayers;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSendSecondsTillStart implements IMessage {

    private int seconds;

    public PacketSendSecondsTillStart() {
    }

    public PacketSendSecondsTillStart(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        seconds = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(seconds);
    }

    public static class Handler implements IMessageHandler<PacketSendSecondsTillStart, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendSecondsTillStart message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiLobbyPlayers.secondsTillStart = message.seconds;
            });
            return null;
        }
    }
}
