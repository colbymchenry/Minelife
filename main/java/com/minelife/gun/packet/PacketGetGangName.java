package com.minelife.gun.packet;

import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
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

public class PacketGetGangName implements IMessage {

    private UUID GangUUID;
    private Class<? extends IGangNameReceiver> receiver;
    private String name;

    public PacketGetGangName() {
    }

    public PacketGetGangName(UUID gangUUID, IGangNameReceiver receiver) {
        GangUUID = gangUUID;
        this.receiver = receiver.getClass();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        GangUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        try {
            receiver = (Class<? extends IGangNameReceiver>) Class.forName(ByteBufUtils.readUTF8String(buf));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(buf.readBoolean()) name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, GangUUID.toString());
        ByteBufUtils.writeUTF8String(buf, this.receiver.getName());
        Gang g = ModGangs.getGang(GangUUID);
        buf.writeBoolean(g != null);
        ByteBufUtils.writeUTF8String(buf, g.getName());
    }

    public static class Handler implements IMessageHandler<PacketGetGangName, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketGetGangName message, MessageContext ctx) {
            if(Minecraft.getMinecraft().currentScreen.getClass().equals(message.receiver)) {
                IGangNameReceiver gangNameReceiver = (IGangNameReceiver) Minecraft.getMinecraft().currentScreen;
                gangNameReceiver.nameReceived(message.GangUUID, message.name);
            }
            return null;
        }
    }

}
