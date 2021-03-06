package com.minelife.emt;

import com.minelife.util.client.render.SkinChanger;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketSendEMTStatus implements IMessage {

    private UUID playerID;
    private boolean isEMT;

    public PacketSendEMTStatus() {
    }

    public PacketSendEMTStatus(UUID playerID, boolean isEMT) {
        this.playerID = playerID;
        this.isEMT = isEMT;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        isEMT = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
        buf.writeBoolean(isEMT);
    }

    public static class Handler implements IMessageHandler<PacketSendEMTStatus, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendEMTStatus message, MessageContext ctx) {
            if(message.isEMT) {
                ClientProxy.EMT_SET.add(message.playerID);
                SkinChanger.playerTextures.remove(message.playerID);
            }
            else {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    if(Minecraft.getMinecraft().world.getPlayerEntityByUUID(message.playerID) != null) {
                        SkinChanger.resetSkin((AbstractClientPlayer) Minecraft.getMinecraft().world.getPlayerEntityByUUID(message.playerID));
                    }
                });
                ClientProxy.EMT_SET.remove(message.playerID);
            }
            return null;
        }
    }

}
