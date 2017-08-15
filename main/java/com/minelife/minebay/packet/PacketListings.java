package com.minelife.minebay.packet;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PacketListings implements IMessage {

    public PacketListings() {}

    private int page;

    public PacketListings(int page) {
        this.page = page;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.page = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.page);
    }

    public static class Handler implements IMessageHandler<PacketListings, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketListings message, MessageContext ctx)
        {
            int min_row = 100 * message.page;
            int max_row = 100 * (message.page + 1);
            try {
                ResultSet result = Minelife.SQLITE.query("SELECT * FROM item_listings LIMIT " + min_row + "," + max_row);
                List<ItemListing> listings = Lists.newArrayList();
                while(result.next()) listings.add(new ItemListing(UUID.fromString(result.getString("uuid"))));
                Minelife.NETWORK.sendTo(new PacketResponseListings(listings), ctx.getServerHandler().playerEntity);
            } catch (SQLException e) {
                e.printStackTrace();
                Minelife.handle_exception(e, ctx.getServerHandler().playerEntity);
                ctx.getServerHandler().playerEntity.closeScreen();
            }
            return null;
        }
    }
}
