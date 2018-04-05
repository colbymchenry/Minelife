package com.minelife.economy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BillEvent extends Event {

    private EntityPlayer player;
    private Bill bill;
    private int amount;

    public BillEvent(Bill bill, EntityPlayer player, int amount) {
        this.bill = bill;
        this.player = player;
        this.amount = amount;
    }

    public Bill getBill() {
        return this.bill;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public int getAmount() {
        return amount;
    }

    public static class PayEvent extends BillEvent {

        public PayEvent(Bill bill, EntityPlayer player, int amount) {
            super(bill, player, amount);
        }

    }

    public static class LateEvent extends BillEvent {

        public LateEvent(Bill bill, EntityPlayer player, int amount) {
            super(bill, player, amount);
        }

    }

}
