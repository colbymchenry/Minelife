package com.minelife.police.network;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.police.TicketSearchResult;
import com.minelife.util.NBTUtil;
import com.minelife.util.server.Callback;
import com.minelife.util.server.FetchUUIDThread;
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

public class PacketRequestTicketSearch implements IMessage {

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

    public static class Handler implements IMessageHandler<PacketRequestTicketSearch, IMessage>, Callback {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestTicketSearch message, MessageContext ctx) {

            StringBuilder query = new StringBuilder("SELECT * FROM policetickets");
            String officerName = "", playerName = "";

            FetchUUIDThread.instance.fetchUUID(message.player, this, message, ctx, query, false, officerName, playerName);

            return null;
        }

        @Override
        public void callback(Object... objects) {
            UUID uuid = (UUID) objects[0];
            String name = (String) objects[1];
            PacketRequestTicketSearch message = (PacketRequestTicketSearch) objects[2];
            MessageContext ctx = (MessageContext) objects[3];
            StringBuilder query = (StringBuilder) objects[4];
            boolean isOfficerSearch = (boolean) objects[5];

            if (!isOfficerSearch) {
                FetchUUIDThread.instance.fetchUUID(message.officer, this, message, ctx, query, true);
                if (uuid != null && name != null) {
                    query.append(" WHERE playerUUID='" + uuid.toString() + "'");
                    objects[7] = name;
                }
            } else {
                if (uuid != null && name != null) {
                    objects[6] = name;
                    if (query.toString().contains("WHERE")) {
                        query.append(" AND officerUUID='" + uuid.toString() + "'");
                    } else {
                        query.append(" WHERE officerUUID='" + uuid.toString() + "'");
                    }
                }

                if (message.ticketID != 0) {
                    if (query.toString().contains("WHERE")) {
                        query.append(" AND ticketID='" + message.ticketID + "'");
                    } else {
                        query.append(" WHERE ticketID='" + message.ticketID + "'");
                    }
                }

                try {
                    ResultSet result = Minelife.SQLITE.query(query.toString());
                    List<TicketSearchResult> resultList = Lists.newArrayList();
                    while (result.next()) {
                        ItemStack ticketStack = ItemStack.loadItemStackFromNBT(NBTUtil.fromString(result.getString("ticketNBT")));
                        TicketSearchResult searchResult = new TicketSearchResult(ticketStack, (String) objects[6], (String) objects[7]);
                        resultList.add(searchResult);
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
