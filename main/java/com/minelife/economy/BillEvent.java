package com.minelife.economy;

import net.minecraftforge.fml.common.eventhandler.Event;

public class BillEvent extends Event{

    private Bill bill;

    public BillEvent(Bill bill) {
        this.bill = bill;
    }

    public Bill getBill() {
        return this.bill;
    }

    public static class PayEvent extends BillEvent {

        public PayEvent(Bill bill) {
            super(bill);
        }

    }

    public static class LateEvent extends BillEvent {

        public LateEvent(Bill bill) {
            super(bill);
        }

    }

}
