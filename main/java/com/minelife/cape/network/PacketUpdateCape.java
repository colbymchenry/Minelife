package com.minelife.cape.network;

import com.minelife.cape.ModCapes;
import com.minelife.util.client.render.CustomLayerCape;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketUpdateCape implements IMessage {

    private UUID playerID;
    private int entityID;
    private String pixels;

    public PacketUpdateCape() {
    }

    public PacketUpdateCape(UUID playerID, int entityID, String pixels) {
        this.playerID = playerID;
        this.entityID = entityID;
        this.pixels = pixels;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        entityID = buf.readInt();
        if (buf.readBoolean())
            pixels = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
        buf.writeInt(entityID);
        buf.writeBoolean(pixels != null);
        if (pixels != null)
            ByteBufUtils.writeUTF8String(buf, pixels);
    }

    public static class Handler implements IMessageHandler<PacketUpdateCape, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdateCape message, MessageContext ctx) {
            CustomLayerCape.textures.remove(message.playerID);
            if (message.pixels != null && Minecraft.getMinecraft().player != null) {
                if (Minecraft.getMinecraft().player.getEntityWorld().getEntityByID(message.entityID) != null) {
                    if (Minecraft.getMinecraft().player.getEntityWorld().getEntityByID(message.entityID) instanceof EntityPlayer) {
                        ModCapes.itemCape.setPixels((EntityPlayer) Minecraft.getMinecraft().player.getEntityWorld().getEntityByID(message.entityID), message.pixels);
                    }
                }
            }
            return null;
        }
    }

}
