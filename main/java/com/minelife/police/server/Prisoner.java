package com.minelife.police.server;

import com.minelife.police.ChargeType;

import java.util.List;
import java.util.UUID;

public class Prisoner {

    private UUID playerID;
    private int paymentTowardsBail;
    private List<ChargeType> charges;

    public Prisoner(UUID playerID, int paymentTowardsBail, List<ChargeType> charges) {
        this.playerID = playerID;

        this.paymentTowardsBail = paymentTowardsBail;
        this.charges = charges;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public int getPaymentTowardsBail() {
        return paymentTowardsBail;
    }

    public void setPaymentTowardsBail(int paymentTowardsBail) {
        this.paymentTowardsBail = paymentTowardsBail;
    }

    public List<ChargeType> getCharges() {
        return charges;
    }

    public int getTotalBail() { return  charges.stream().mapToInt(charge -> charge.chargeAmount).sum(); }
}
