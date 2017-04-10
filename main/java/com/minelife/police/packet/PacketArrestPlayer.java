package com.minelife.police.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.minecraft.realms.Tezzelator.t;

public class PacketArrestPlayer implements IMessage {

    private int entityID;
    private boolean arrested;

    public PacketArrestPlayer() {
    }

    public PacketArrestPlayer(int entityID, boolean arrested) {
        this.entityID = entityID;
        this.arrested = arrested;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityID = buf.readInt();
        arrested = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityID);
        buf.writeBoolean(arrested);
    }

    public static class Handler implements IMessageHandler<PacketArrestPlayer, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketArrestPlayer message, MessageContext ctx) {

            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
            entity.getEntityData().setBoolean("arrested", message.arrested);
            if (message.arrested) {
                entity.mountEntity(Minecraft.getMinecraft().thePlayer);
                entity.ignoreFrustumCheck = true;
            }
            else
                entity.mountEntity(null);

            return null;
        }
    }

}
