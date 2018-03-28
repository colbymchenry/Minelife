package com.minelife.guns.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketAttachment implements IMessage {

    private int slotAttachment, slotGun;

    public PacketAttachment() {
    }

    public PacketAttachment(int slotAttachment, int slotGun) {
        this.slotAttachment = slotAttachment;
        this.slotGun = slotGun;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotAttachment = buf.readInt();
        slotGun = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotAttachment);
        buf.writeInt(slotGun);
    }

    public static class Handler implements IMessageHandler<PacketAttachment, IMessage> {

        @Override
        public IMessage onMessage(PacketAttachment message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {

            });
            return null;
        }

    }

}
