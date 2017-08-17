package com.minelife.minebay.packet;

import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.sql.SQLException;
import java.util.UUID;

public class PacketBuyItem implements IMessage {

    private UUID listing_uuid;
    private int amount;

    public PacketBuyItem() {}

    public PacketBuyItem(UUID listing_uuid, int amount) {
        this.listing_uuid = listing_uuid;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        listing_uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, listing_uuid.toString());
        buf.writeInt(amount);
    }

    public static class Handler implements IMessageHandler<PacketBuyItem, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketBuyItem message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            try {
                new ItemListing(message.listing_uuid).finalize(player, message.amount);
            } catch (SQLException e) {
                Minelife.handle_exception(e, player);
            }
            return null;
        }
    }

}
