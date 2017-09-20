package com.minelife.police.network;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.police.TicketSearchResult;
import com.minelife.util.NBTUtil;
import com.minelife.util.server.Callback;
import com.minelife.util.server.FetchUUIDThread;
import com.minelife.util.server.NameFetcher;
import com.minelife.util.server.UUIDFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PacketRequestTicketSearch implements IMessage {

    private static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private String player, officer;
    private int ticketID;

    public PacketRequestTicketSearch() {
    }

    public PacketRequestTicketSearch(String player, String officer, int ticketID) {
        this.player = player;
        this.officer = officer;
        this.ticketID = ticketID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        player = ByteBufUtils.readUTF8String(buf);
        officer = ByteBufUtils.readUTF8String(buf);
        ticketID = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, player == null || player.isEmpty() ? " " : player);
        ByteBufUtils.writeUTF8String(buf, officer == null || officer.isEmpty() ? " " : officer);
        buf.writeInt(ticketID);
    }

    public static class Handler implements IMessageHandler<PacketRequestTicketSearch, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestTicketSearch message, MessageContext ctx) {
            pool.submit(() -> {
                try {
                    handleMessage(message, ctx);
                } catch (Exception e) {
                    e.printStackTrace();
                    Minelife.handle_exception(e, ctx.getServerHandler().playerEntity);
                }
            });
            return null;
        }

        public synchronized void handleMessage(PacketRequestTicketSearch message, MessageContext ctx) throws Exception {
            StringBuilder query = new StringBuilder("SELECT * FROM policetickets");
            UUID playerUUID = !message.player.trim().isEmpty() ? UUIDFetcher.get(message.player) : null;
            UUID officerUUID = !message.officer.trim().isEmpty() ? UUIDFetcher.get(message.officer) : null;

            if(playerUUID != null) {
                query.append(" WHERE playerUUID='" + playerUUID.toString() + "'");
            }

            if(officerUUID != null) {
                if(playerUUID != null) {
                    query.append(" AND officerUUID='" + officerUUID.toString() + "'");
                } else {
                    query.append(" WHERE officerUUID='" + officerUUID.toString() + "'");
                }
            }

            if(message.ticketID != 0) {
                if(playerUUID != null || officerUUID != null) {
                    query.append(" AND ticketID='" + message.ticketID + "'");
                } else {
                    query.append(" WHERE ticketID='" + message.ticketID + "'");
                }
            }

            List<TicketSearchResult> resultList = Lists.newArrayList();

            ResultSet result = Minelife.SQLITE.query(query.toString());
            while(result.next()) {
                String playerName = NameFetcher.get(UUID.fromString(result.getString("playerUUID")));
                String officerName = NameFetcher.get(UUID.fromString(result.getString("officerUUID")));
                resultList.add(new TicketSearchResult(ItemStack.loadItemStackFromNBT(NBTUtil.fromString(result.getString("ticketNBT"))), playerName, officerName));
            }

            Minelife.NETWORK.sendTo(new PacketResponseTicketSearch(resultList), ctx.getServerHandler().playerEntity);
        }
    }
}
