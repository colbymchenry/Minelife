package com.minelife.util.server;

import com.minelife.util.client.IUUIDReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class PacketResponseUUID implements IMessage {

    private UUID playerUUID;
    private String playerName;
    private String receiver;

    public PacketResponseUUID() {
    }

    public PacketResponseUUID(UUID playerUUID, String playerName, String receiver) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.receiver = receiver;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        playerName = ByteBufUtils.readUTF8String(buf);
        receiver = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
        ByteBufUtils.writeUTF8String(buf, playerName);
        ByteBufUtils.writeUTF8String(buf, receiver);
    }

    public static class Handler implements IMessageHandler<PacketResponseUUID, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketResponseUUID message, MessageContext ctx) {
            if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof IUUIDReceiver) {
                if (Minecraft.getMinecraft().currentScreen.getClass().getName().equals(message.receiver)) {
                    ((IUUIDReceiver) Minecraft.getMinecraft().currentScreen).uuidReceived(message.playerName, message.playerUUID);
                }
            }
            return null;
        }
    }

}
