package com.minelife.minebay.packet;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.util.server.Callback;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PacketListings implements IMessage {

    public PacketListings() {}

    private int page;
    private String search_for;
    private int order_by;


    public PacketListings(int page, String search_for, int order_by) {
        this.page = page;
        this.search_for = search_for;
        this.order_by = order_by;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.page = buf.readInt();
        this.search_for = ByteBufUtils.readUTF8String(buf);
        this.order_by = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.page);
        ByteBufUtils.writeUTF8String(buf, this.search_for);
        buf.writeInt(this.order_by);
    }

    public static class Handler implements IMessageHandler<PacketListings, IMessage>, Callback {

        private EntityPlayer player;

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketListings message, MessageContext ctx)
        {
            int min_row = 100 * message.page;
            int max_row = 100 * (message.page + 1);
            this.player = ctx.getServerHandler().playerEntity;
            try {
                ResultSet result = Minelife.SQLITE.query("SELECT * FROM item_listings ORDER BY price LIMIT " + min_row + "," + max_row);
                if(!message.search_for.trim().isEmpty()) {
                    result = Minelife.SQLITE.query("SELECT * FROM item_listings WHERE title LIKE '%" + message.search_for + "%' LIMIT " + min_row + "," + max_row);
                }
                new Thread(new GetListings(this, result)).start();
            } catch (SQLException e) {
                e.printStackTrace();
                Minelife.handle_exception(e, ctx.getServerHandler().playerEntity);
                ctx.getServerHandler().playerEntity.closeScreen();
            }
            return null;
        }

        @Override
        public void callback(Object... objects)
        {
            Minelife.NETWORK.sendTo(new PacketResponseListings((List<ItemListing>) objects[0]), (EntityPlayerMP) player);
        }
    }

    static class GetListings implements Runnable {

        private ResultSet result;
        private Callback callback;
        private List<ItemListing> listings = Lists.newArrayList();

        public GetListings(Callback callback, ResultSet result) {
            this.callback = callback;
            this.result = result;
        }

        @Override
        public void run()
        {
            try {
                while(result.next()) listings.add(new ItemListing(UUID.fromString(result.getString("uuid"))));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            callback.callback(listings);
        }
    }
}
