package com.minelife.police.network;

import com.minelife.Minelife;
import com.minelife.police.ModPolice;
import com.minelife.util.PlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketCheckUnconscious implements IMessage {

    private UUID entityUUID;

    public PacketCheckUnconscious() {
    }

    public PacketCheckUnconscious(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, entityUUID.toString());
    }

    public static class Handler implements IMessageHandler<PacketCheckUnconscious, IMessage> {

        @Override
        public IMessage onMessage(PacketCheckUnconscious message, MessageContext ctx) {
            EntityPlayer player = PlayerHelper.getPlayer(message.entityUUID);

            if (player != null)
                Minelife.getNetwork().sendTo(new PacketUnconscious(player.getEntityId(), ModPolice.isUnconscious(player)), ctx.getServerHandler().player);
            return null;
        }
    }

}
