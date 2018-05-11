package com.minelife.police;

public enum ChargeType {

    POSSESSION_OF_MARIJUANA(5000),
    POSSESSION_OF_COCAINE(8000),
    POSSESSION_OF_UNLAWFUL_FIREARM(1000),
    MURDER(15000),
    THEFT(4000),
    BREAKING_AND_ENTERING(3000),
    ASSAULT(3000),
    PRISON_BREAK(15000);

    public int chargeAmount;

    ChargeType(int chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

}
