package com.minelife.gun.packet;

import com.minelife.Minelife;
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
    private String receiver;
    private String name;

    public PacketGetGangName() {
    }

    public PacketGetGangName(UUID gangUUID, IGangNameReceiver receiver) {
        GangUUID = gangUUID;
        this.receiver = receiver.getClass().getName();
    }

    public PacketGetGangName(String name, IGangNameReceiver receiver) {
        this.name = name;
        this.receiver = receiver.getClass().getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        receiver = ByteBufUtils.readUTF8String(buf);

        if (buf.readBoolean()) {
            name = ByteBufUtils.readUTF8String(buf);
        } else {
            GangUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        }

        Gang g = GangUUID != null ? ModGangs.getGang(GangUUID) : ModGangs.getGang(name);

        if (g != null) {
            name = g.getName();
            GangUUID = g.getGangID();
        } else {
            name = null;
            GangUUID = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, receiver);

        buf.writeBoolean(name != null);

        if (name != null)
            ByteBufUtils.writeUTF8String(buf, name);
        else
            ByteBufUtils.writeUTF8String(buf, GangUUID.toString());
    }

    public static class Handler implements IMessageHandler<PacketGetGangName, IMessage> {

        @Override
        public IMessage onMessage(PacketGetGangName message, MessageContext ctx) {
            Minelife.NETWORK.sendTo(new PacketRespondGetGangName(message.name, message.GangUUID, message.receiver), ctx.getServerHandler().playerEntity);
            return null;
        }
    }

}
