package com.minelife.jobs;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PacketUpdateNPC implements IMessage {

    private int entityID;
    private NBTTagCompound tag;

    public PacketUpdateNPC() {
    }

    public PacketUpdateNPC(int entityID, NBTTagCompound tag) {
        this.entityID = entityID;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        ByteBufUtils.writeTag(buf, tag);
    }

    public static class Handler implements IMessageHandler<PacketUpdateNPC, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdateNPC message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;
            if(world.getEntityByID(message.entityID) != null) {
            }
            return null;
        }
    }

}
