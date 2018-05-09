package com.minelife.police;


import com.minelife.emt.PacketSendEMTStatus;
import com.minelife.police.client.ClientProxy;
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

public class PacketSendCopStatus implements IMessage {

    private UUID playerID;
    private boolean isCop;

    public PacketSendCopStatus() {
    }

    public PacketSendCopStatus(UUID playerID, boolean isCop) {
        this.playerID = playerID;
        this.isCop = isCop;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        isCop = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
        buf.writeBoolean(isCop);
    }

    public static class Handler implements IMessageHandler<PacketSendCopStatus, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendCopStatus message, MessageContext ctx) {
            if(message.isCop) {
                ClientProxy.POLICE_SET.add(message.playerID);
                SkinChanger.playerTextures.remove(message.playerID);
            }
            else {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    if(Minecraft.getMinecraft().world.getPlayerEntityByUUID(message.playerID) != null) {
                        SkinChanger.resetSkin((AbstractClientPlayer) Minecraft.getMinecraft().world.getPlayerEntityByUUID(message.playerID));
                    }
                });
                ClientProxy.POLICE_SET.remove(message.playerID);
            }
            return null;
        }
    }

}
