package com.minelife.police.network;

import com.minelife.police.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUnconscious implements IMessage {

    private int playerID;
    private boolean status;

    public PacketUnconscious() {
    }

    public PacketUnconscious(int playerID, boolean status) {
        this.playerID = playerID;
        this.status = status;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = buf.readInt();
        status = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerID);
        buf.writeBoolean(status);
    }

    public static class Handler implements IMessageHandler<PacketUnconscious, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUnconscious message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (message.status) {
                    Minecraft.getMinecraft().world.getEntityByID(message.playerID).getEntityData().setBoolean("Unconscious", true);
                    Minecraft.getMinecraft().world.getEntityByID(message.playerID).getEntityData().setLong("UnconsciousTime", System.currentTimeMillis() + (60000L * 3));
                    ClientProxy.setSleeping((EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(message.playerID), true);
                } else {
                    Minecraft.getMinecraft().world.getEntityByID(message.playerID).getEntityData().removeTag("Unconscious");
                    Minecraft.getMinecraft().world.getEntityByID(message.playerID).getEntityData().removeTag("UnconsciousTime");
                    ClientProxy.setSleeping((EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(message.playerID), false);
                }
            });
            return null;
        }
    }

}
