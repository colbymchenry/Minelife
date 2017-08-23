package com.minelife.minebay;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public abstract class Listing {

    protected UUID uuid, seller;
    protected double price;
    protected String title, description;

    protected Listing()
    {
    }

    public Listing(UUID uuid, UUID seller, double price, String title, String description)
    {
        this.uuid = uuid;
        this.seller = seller;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public double price()
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

    public abstract void finalize(EntityPlayer player, Object... objects);

}
