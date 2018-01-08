package com.minelife.capes.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

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
            Minecraft.getMinecraft().thePlayer.worldObj.getEntityByID(message.entityID).getEntityData().setBoolean("cape", message.on);
            return null;
        }
    }

}
