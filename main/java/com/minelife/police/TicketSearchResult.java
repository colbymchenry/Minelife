package com.minelife.police;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class TicketSearchResult {

    public int ticketID;
    public ItemStack ticketStack;
    public String officerName, playerName;

    public TicketSearchResult(int ticketID, ItemStack ticketStack, String officerName, String playerName) {
        this.ticketID = ticketID;
        this.ticketStack = ticketStack;
        this.officerName = officerName;
        this.playerName = playerName;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(ticketID);
        ByteBufUtils.writeItemStack(buf, ticketStack);
        ByteBufUtils.writeUTF8String(buf, officerName);
        ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static TicketSearchResult fromBytes(ByteBuf buf) {
        int ticketID = buf.readInt();
        ItemStack ticketStack = ByteBufUtils.readItemStack(buf);
        String officerName = ByteBufUtils.readUTF8String(buf);
        String playerName = ByteBufUtils.readUTF8String(buf);
        return new TicketSearchResult(ticketID, ticketStack, officerName, playerName);
    }

}
