package com.minelife.util.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketRidingEntity implements IMessage {

    private int rider, mount;

    public PacketRidingEntity() {
    }

    public PacketRidingEntity(int rider, int mount) {
        this.rider = rider;
        this.mount = mount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        rider = buf.readInt();
        mount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(rider);
        buf.writeInt(mount);
    }

    public static class Handler implements IMessageHandler<PacketRidingEntity, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketRidingEntity message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               Entity rider = Minecraft.getMinecraft().world.getEntityByID(message.rider);
               Entity mount = Minecraft.getMinecraft().world.getEntityByID(message.mount);
               if(rider != null && mount != null) {
                   rider.startRiding(mount);
               }
            });
            return null;
        }
    }

}
