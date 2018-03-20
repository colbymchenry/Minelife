package com.minelife.util.client;

import com.minelife.Minelife;
import com.minelife.util.server.*;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class PacketRequestName implements IMessage {

    public UUID playerUUID;

    public PacketRequestName() {
    }

    public PacketRequestName(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerUUID.toString());
    }

    public static class Handler implements IMessageHandler<PacketRequestName, IMessage>, NameUUIDCallback {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestName message, MessageContext ctx) {
            NameFetcher.get(message.playerUUID, this, ctx.getServerHandler().player);
            return null;
        }

        @Override
        public void callback(UUID id, String name, Object... objects) {
            EntityPlayerMP player = (EntityPlayerMP) objects[0];
            Minelife.getNetwork().sendTo(new PacketResponseName(id, name), player);
        }
    }


}
