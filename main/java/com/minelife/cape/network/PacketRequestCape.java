package com.minelife.cape.network;

import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.util.PlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketRequestCape implements IMessage {

    private UUID playerID;

    public PacketRequestCape() {
    }

    public PacketRequestCape(UUID playerID) {
        this.playerID = playerID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
    }

    public static class Handler implements IMessageHandler<PacketRequestCape, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestCape message, MessageContext ctx) {
            if(PlayerHelper.getPlayer(message.playerID) == null) return null;
            EntityPlayerMP player = PlayerHelper.getPlayer(message.playerID);
            boolean on = player.getEntityData().hasKey("Cape") ? player.getEntityData().getBoolean("Cape") : false;
            Minelife.getNetwork().sendTo(new PacketUpdateCapeStatus(player.getEntityId(), on), ctx.getServerHandler().player);
            Minelife.getNetwork().sendTo(new PacketUpdateCape(player.getUniqueID(), player.getEntityId(), ModCapes.itemCape.getPixels(player)), ctx.getServerHandler().player);
            return null;
        }

    }


}
