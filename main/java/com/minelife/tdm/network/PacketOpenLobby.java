package com.minelife.tdm.network;

import com.minelife.tdm.Match;
import com.minelife.tdm.client.gui.GuiLobby;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenLobby implements IMessage {

    private Match match;
    private String arena;

    public PacketOpenLobby() {
    }

    public PacketOpenLobby(Match match, String arena) {
        this.match = match;
        this.arena = arena;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        match = Match.fromBytes(buf);
        arena = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        match.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, arena);
    }

    public static class Handler implements IMessageHandler<PacketOpenLobby, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenLobby message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiLobby(message.match, message.arena)));
            return null;
        }

    }

}
