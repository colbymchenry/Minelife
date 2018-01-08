package com.minelife.capes.network;

import com.minelife.capes.client.ItemCapeRenderer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketRemoveCapeItemTexture implements IMessage {

    private String uuid;

    public PacketRemoveCapeItemTexture(String uuid) {
        this.uuid = uuid;
    }

    public PacketRemoveCapeItemTexture() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, uuid);
    }

    public static class Handler implements IMessageHandler<PacketRemoveCapeItemTexture, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketRemoveCapeItemTexture message, MessageContext ctx) {
            ItemCapeRenderer.textures.remove(message.uuid);
            return null;
        }
    }

}
