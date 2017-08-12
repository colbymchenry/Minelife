package com.minelife.minebay.client.gui;

import com.minelife.minebay.ItemListing;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;

public class ListingsGui extends MasterGui {

    private Listings listings_gui;
    private GuiLoadingAnimation loadingAnimation;
    private List<ItemListing> listings;

    public ListingsGui() {

    }

    public ListingsGui(List<ItemListing> listings) {
        this.listings = listings;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        super.drawScreen(mouse_x, mouse_y, f);

        if (listings == null) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        this.listings_gui.draw(mouse_x, mouse_y, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
        if (listings_gui != null)
            this.listings_gui.keyTyped(c, i);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if (listings == null) {
            loadingAnimation = new GuiLoadingAnimation((this.width - 75) / 2, 20 + (this.height - 75) / 2, 75, 75, Color.white);
            return;
        }

        this.listings_gui = new Listings(this.left, this.top, this.bg_width, this.bg_height, listings, this);
    }

    class Listings extends GuiScrollableContent {

        private final List<ItemListing> item_listings;
        private final GuiScreen parent;

        public Listings(int xPosition, int yPosition, int width, int height, List<ItemListing> item_listings, GuiScreen parent)
        {
            super(xPosition, yPosition, width, height);
            this.item_listings = item_listings;
            this.parent = parent;
        }

        @Override
        public int getObjectHeight(int index)
        {
            return item_listings.get(index).height();
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            item_listings.get(index).draw(mc, mouseX, mouseY, parent.width, parent.height);
        }

        @Override
        public int getSize()
        {
            return item_listings.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            item_listings.get(index).mouse_clicked(mouseX, mouseY, doubleClick);
        }

        @Override
        public void drawBackground()
        {
        }

        @Override
        public void drawScrollbar(float gripSize)
        {
            GuiUtil.drawDefaultBackground(0, -5, 8, gripSize + 7, new Color(186, 0, 184));
        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {
            GuiUtil.drawDefaultBackground(0, 0, width - 8, height, new Color(186, 0, 184));
        }
    }

}
