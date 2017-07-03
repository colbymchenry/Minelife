package com.minelife.realestate;

import java.util.Date;
import java.util.UUID;

public class EstateSellingForm {

    private UUID uuid;
    private Estate estate;
    private Date startDate, endDate;
    private long price;
    private boolean renting;
    private UUID renter;

    public EstateSellingForm(UUID uuid) {
        this.uuid = uuid;

    }

}
