package com.minelife.util.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDropEntity implements IMessage {

    private int entityID;

    public PacketDropEntity() {
    }

    public PacketDropEntity(int entityID) {
        this.entityID = entityID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
    }

    public static class Handler implements IMessageHandler<PacketDropEntity, IMessage> {

        @Override
        public IMessage onMessage(PacketDropEntity message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               if(Minecraft.getMinecraft().world.getEntityByID(message.entityID) != null) {
                   Minecraft.getMinecraft().world.getEntityByID(message.entityID).dismountRidingEntity();
               }
            });
            return null;
        }

    }

}
