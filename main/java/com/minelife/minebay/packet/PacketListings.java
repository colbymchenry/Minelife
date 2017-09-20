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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketListings implements IMessage {

    private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static String[] options = new String[]{"Price", "Date", "Damage", "Stack Size"};

    public PacketListings() {
    }

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
    public void fromBytes(ByteBuf buf) {
        this.page = buf.readInt();
        this.search_for = ByteBufUtils.readUTF8String(buf);
        this.order_by = buf.readInt();
        this.ascend = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.page);
        ByteBufUtils.writeUTF8String(buf, this.search_for);
        buf.writeInt(this.order_by);
        buf.writeBoolean(this.ascend);
    }

    public static class Handler implements IMessageHandler<PacketListings, IMessage> {

        private EntityPlayer player;

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketListings message, MessageContext ctx) {
            int min_row = 100 * message.page;
            int max_row = 100 * (message.page + 1);
            this.player = ctx.getServerHandler().playerEntity;
            // ASCENDING = ASC
            // DESCENDING = DESC
            String query = "";

            if (!message.search_for.trim().isEmpty())
                query += "WHERE title LIKE '%" + message.search_for + "%' ";

            switch (message.order_by) {
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
            final String query_count = query;
            query += "LIMIT " + min_row + "," + max_row;
            final String query_final = query;
            pool.submit(() -> {
                try {
                    getListings(Minelife.SQLITE.query("SELECT * FROM item_listings " + query_final), Minelife.SQLITE.query("SELECT COUNT(*) AS count FROM item_listings " + query_count));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return null;
        }

        public synchronized void getListings(ResultSet result, ResultSet result_count) throws Exception {
            double pages = 0;
            List<ItemListing> listings = Lists.newArrayList();
            while (result.next()) {
                listings.add(new ItemListing(UUID.fromString(result.getString("uuid"))));
            }
            pages = Math.floor(result_count.getInt("count") / 100D);
            pages += result_count.getInt("count") % 100 != 0 ? 1 : 0;
            Minelife.NETWORK.sendTo(new PacketResponseListings(listings, (int) pages), (EntityPlayerMP) player);
        }
    }

}
