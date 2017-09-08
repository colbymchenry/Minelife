package com.minelife.police;

import net.minecraft.item.ItemStack;

public class TicketSearchResult {

    public ItemStack ticketStack;
    public String officerName, playerName;

    public TicketSearchResult(ItemStack ticketStack, String officerName, String playerName) {
        this.ticketStack = ticketStack;
        this.officerName = officerName;
        this.playerName = playerName;
    }
}
