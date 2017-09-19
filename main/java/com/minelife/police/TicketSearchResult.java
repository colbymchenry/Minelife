package com.minelife.police;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

public class TicketSearchResult {

    public ItemStack ticketStack;
    public String officerName, playerName;

    public TicketSearchResult(ItemStack ticketStack, String officerName, String playerName) {
        this.ticketStack = ticketStack;
        this.officerName = officerName;
        this.playerName = playerName;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, ticketStack);
        ByteBufUtils.writeUTF8String(buf, officerName);
        ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static TicketSearchResult fromBytes(ByteBuf buf) {
        ItemStack ticketStack = ByteBufUtils.readItemStack(buf);
        String officerName = ByteBufUtils.readUTF8String(buf);
        String playerName = ByteBufUtils.readUTF8String(buf);
        return new TicketSearchResult(ticketStack, officerName, playerName);
    }

}
