package com.minelife.minebay.client.gui;

import com.minelife.minebay.ItemListing;
import com.minelife.minebay.Listing;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;

import java.util.List;

public class GuiScrollableListing extends GuiScrollableContent {

    private List<ItemListing> itemListings;

    public GuiScrollableListing(Minecraft mc, int x, int y, int width, int height, List<ItemListing> itemListings) {
        super(mc, x, y, width, height);
        this.itemListings = itemListings;
    }

    @Override
    public int getObjectHeight(int index) {
        return this.itemListings.get(index).getHeight();
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        this.itemListings.get(index).draw(mouseX, mouseY);
    }

    @Override
    public int getSize() {
        return this.itemListings.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
        if (doubleClick) {
//            Minecraft.getMinecraft().displayGuiScreen(new BuyItemGui(item_listings.get(index)));
        } else {
            this.itemListings.get(index).mouseClicked(mouseX, mouseY, false);
        }
    }

    @Override
    public void drawBackground() {
    }

    @Override
    public void drawGrip(int gripHeight) {
        GuiHelper.drawDefaultBackground(0, -5, 8, gripHeight + 7, 0xba00b8);
    }

    @Override
    public void drawSelectionBox(int index, int width, int height) {
        GuiHelper.drawDefaultBackground(0, 0, width - 8, height, 0xba00b8);
    }

}
