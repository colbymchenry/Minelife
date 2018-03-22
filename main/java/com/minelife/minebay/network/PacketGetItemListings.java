package com.minelife.minebay.network;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.ModMinebay;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketGetItemListings implements IMessage {

    private static final ExecutorService pool = Executors.newFixedThreadPool(1);

    public static String[] options = new String[]{"Price", "Date", "Damage", "Stack Size"};
    private int page;
    private String criteria;
    private int orderBy;
    private boolean ascend;

    public PacketGetItemListings() {
    }

    public PacketGetItemListings(int page, String criteria, int orderBy, boolean ascend) {
        this.page = page;
        this.criteria = criteria;
        this.orderBy = orderBy;
        this.ascend = ascend;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.page = buf.readInt();
        this.criteria = ByteBufUtils.readUTF8String(buf);
        this.orderBy = buf.readInt();
        this.ascend = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.page);
        ByteBufUtils.writeUTF8String(buf, this.criteria);
        buf.writeInt(this.orderBy);
        buf.writeBoolean(this.ascend);
    }

    public static class Handler implements IMessageHandler<PacketGetItemListings, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketGetItemListings message, MessageContext ctx) {
            int min_row = 100 * message.page;
            int max_row = 100 * (message.page + 1);
            // ASCENDING = ASC
            // DESCENDING = DESC
            String query = "";

            if (!message.criteria.trim().isEmpty())
                query += "WHERE title LIKE '%" + message.criteria + "%' ";

            switch (message.orderBy) {
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
                    getListings(ModMinebay.getDatabase().query("SELECT * FROM item_listings " + query_final), ModMinebay.getDatabase().query("SELECT COUNT(*) AS count FROM item_listings " + query_count), ctx.getServerHandler().player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return null;
        }

        private synchronized void getListings(ResultSet result, ResultSet result_count, EntityPlayerMP player) throws Exception {
            double pages = 0;
            List<ItemListing> listings = Lists.newArrayList();
            while (result.next()) listings.add(new ItemListing(result));
            pages = Math.floor(result_count.getInt("count") / 100D);
            pages += result_count.getInt("count") % 100 != 0 ? 1 : 0;
            Minelife.getNetwork().sendTo(new PacketSendItemListings(listings, (int) pages), player);
        }
    }

}
