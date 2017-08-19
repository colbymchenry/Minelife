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
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

public class PacketListings implements IMessage {

    public static String[] options = new String[]{"Price", "Date", "Damage", "Stack Size"};

    public PacketListings() {}

    private int page;
    private String search_for;
    private int order_by;
    private boolean ascend;

    public PacketListings(int page, String search_for, int order_by, boolean ascend) {
        this.page = page;
        this.search_for = search_for;
        this.order_by = order_by;
        this.ascend = ascend;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.page = buf.readInt();
        this.search_for = ByteBufUtils.readUTF8String(buf);
        this.order_by = buf.readInt();
        this.ascend = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.page);
        ByteBufUtils.writeUTF8String(buf, this.search_for);
        buf.writeInt(this.order_by);
        buf.writeBoolean(this.ascend);
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
                // TODO: Will have to add more columns to order by the damage and stack size and what not
                // ASCENDING = ASC
                // DESCENDING = DESC
                String query = "";

                if(!message.search_for.trim().isEmpty())
                    query += "WHERE title LIKE '%" + message.search_for + "%' ";

                switch(message.order_by) {
                    case 0:
                        query += "ORDER BY price";
                        break;
                    case 1:
                        query += "ORDER BY datetime(date_published)";
                        break;
                    case 2:
                        query += "ORDER BY damage";
                        break;
                    case 3:
                        query += "ORDER BY stack_size";
                        break;
                }

                query += message.ascend ? " ASC " : " DESC ";
                query += "LIMIT " + min_row + "," + max_row;
                new Thread(new GetListings(this, Minelife.SQLITE.query("SELECT * FROM item_listings " + query),  Minelife.SQLITE.query("SELECT COUNT(*) AS count FROM item_listings " + query))).start();
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
            Minelife.NETWORK.sendTo(new PacketResponseListings((List<ItemListing>) objects[0], (int) objects[1]), (EntityPlayerMP) player);
        }
    }

    static class GetListings implements Runnable {

        private ResultSet result;
        private ResultSet result_count;
        private Callback callback;
        private List<ItemListing> listings = Lists.newArrayList();

        public GetListings(Callback callback, ResultSet result, ResultSet result_count) {
            this.callback = callback;
            this.result = result;
            this.result_count = result_count;
        }

        @Override
        public void run()
        {
            double pages = 0;
            try {
                while(result.next()) listings.add(new ItemListing(UUID.fromString(result.getString("uuid"))));

                pages = Math.floor(result_count.getInt("count") / 100D);
                pages += result_count.getInt("count") % 100 != 0 ? 1 : 0;
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }



            callback.callback(listings, (int) pages);
        }
    }
}
