package com.minelife.gun.packet;

import com.minelife.gun.turrets.IGangNameReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.UUID;

public class PacketRespondGetGangName implements IMessage {

    private String name;
    private UUID id;
    private String receiver;

    public PacketRespondGetGangName() {
    }

    public PacketRespondGetGangName(String name, UUID id, String receiver) {
        this.name = name;
        this.id = id;
        this.receiver = receiver;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        receiver = ByteBufUtils.readUTF8String(buf);
        if(buf.readBoolean()) {
            name = ByteBufUtils.readUTF8String(buf);
            id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, receiver);
        buf.writeBoolean(name != null && id != null);
        if (name != null && id != null) {
            ByteBufUtils.writeUTF8String(buf, name);
            ByteBufUtils.writeUTF8String(buf, id.toString());
        }
    }

    public static class Handler implements IMessageHandler<PacketRespondGetGangName, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketRespondGetGangName message, MessageContext ctx) {
            if (Minecraft.getMinecraft().currentScreen.getClass().getName().equals(message.receiver)) {
                IGangNameReceiver gangNameReceiver = (IGangNameReceiver) Minecraft.getMinecraft().currentScreen;
                gangNameReceiver.nameReceived(message.id, message.name);
            }
            return null;
        }
    }

}
