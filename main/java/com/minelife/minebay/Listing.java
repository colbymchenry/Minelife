package com.minelife.minebay;

import java.util.UUID;

public abstract class Listing {

    protected UUID uuid, seller;
    protected long price;
    protected String title, description;

    protected Listing()
    {
    }

    public Listing(UUID uuid, UUID seller, long price, String title, String description)
    {
        this.uuid = uuid;
        this.seller = seller;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public long price()
    {
        return price;
    }

    public UUID uuid()
    {
        return uuid;
    }

    public UUID seller() {
        return seller;
    }

    public String title()
    {
        return title;
    }

    public String description()
    {
        return description;
    }

    public abstract int height();

    public abstract void draw(int mouse_x, int mouse_y);

    public abstract void mouse_clicked(int mouse_x, int mouse_y, boolean double_click);

}
