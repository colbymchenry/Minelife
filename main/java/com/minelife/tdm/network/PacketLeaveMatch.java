package com.minelife.tdm.network;

import com.minelife.tdm.Match;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketLeaveMatch implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketLeaveMatch, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketLeaveMatch message, MessageContext ctx) {
            Match m = Match.getMatch(ctx.getServerHandler().player);
            if (m != null) {
                m.kickPlayer(ctx.getServerHandler().player);
                ctx.getServerHandler().player.closeScreen();
            }
            return null;
        }
    }

}
