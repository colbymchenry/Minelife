package com.minelife.cape.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpdateCapeStatus implements IMessage {

    private int entityID;
    private boolean on;

    public PacketUpdateCapeStatus() {
    }

    public PacketUpdateCapeStatus(int entityID, boolean on) {
        this.entityID = entityID;
        this.on = on;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        on = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeBoolean(on);
    }

    public static class Handler implements IMessageHandler<PacketUpdateCapeStatus, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdateCapeStatus message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if(Minecraft.getMinecraft().player.getEntityWorld().getEntityByID(message.entityID) != null) {
                    Minecraft.getMinecraft().player.getEntityWorld().getEntityByID(message.entityID).getEntityData().setBoolean("Cape", message.on);
                }
            });
            return null;
        }
    }

}
