package com.minelife.jobs.network;

import com.minelife.jobs.EnumJob;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

public class PacketJoinProfession implements IMessage {

    private EnumJob profession;

    public PacketJoinProfession() {
    }

    public PacketJoinProfession(EnumJob profession) {
        this.profession = profession;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.profession = EnumJob.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.profession.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketJoinProfession, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketJoinProfession message, MessageContext ctx) {
            Objects.requireNonNull(message.profession.getHandler()).joinProfession(ctx.getServerHandler().player);
            ctx.getServerHandler().player.closeScreen();
            return null;
        }

    }

}
