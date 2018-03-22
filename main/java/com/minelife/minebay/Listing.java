package com.minelife.minebay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public abstract class Listing {

    private UUID uuid, seller;
    private int price;
    private String title, description;

    protected Listing() {
    }

    public Listing(UUID uuid, UUID seller, int price, String title, String description) {
        this.uuid = uuid;
        this.seller = seller;
        this.price = price;
        this.title = title;
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public UUID getUniqueID() {
        return uuid;
    }

    public UUID getSellerID() {
        return seller;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public abstract int getHeight();

    public abstract void draw(int mouseX, int mouseY);

    public abstract void mouseClicked(int mouseX, int mouseY, boolean doubleClick);

    public abstract void finalize(EntityPlayer player, Object... objects);

    public Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public FontRenderer getFontRenderer() {
        return getMinecraft().fontRenderer;
    }

}