package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.packet.PacketListings;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class ListingsGui extends MasterGui {

    private static ResourceLocation magnify_texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/magnify.png");

    private Listings listings_gui;
    private GuiLoadingAnimation loadingAnimation;
    private List<ItemListing> listings;
    private GuiTextField search_field;

    public ListingsGui()
    {
        Minelife.NETWORK.sendToServer(new PacketListings(0));
    }

    public ListingsGui(List<ItemListing> listings)
    {
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

//        if(this.listings.isEmpty()) {
//            String msg = EnumChatFormatting.BOLD + "Uh-Oh! No items were found :'(";
//            int msg_width = mc.fontRenderer.getStringWidth(msg);
//            mc.fontRenderer.drawStringWithShadow(msg,  (this.width - msg_width) / 2, top + ((bg_height - mc.fontRenderer.FONT_HEIGHT) / 2), 0xFFFFFF);
//
//            return;
//        }


//        this.listings_gui.draw(mouse_x, mouse_y, Mouse.getDWheel());

        Color color = new Color(72, 0, 70, 200);
        this.drawGradientRect(left - 2, top - 2, left + bg_width, top + 25, color.hashCode(), color.hashCode());
        this.search_field.drawTextBox();
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(magnify_texture);
        GuiUtil.drawImage(left + search_field.getWidth() + 13, search_field.yPosition + 3, 16, 16);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
        if (listings_gui != null)
            this.listings_gui.keyTyped(c, i);

        this.search_field.textboxKeyTyped(c, i);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        this.search_field.mouseClicked(mouse_x, mouse_y, mouse_btn);
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
        this.search_field = new GuiTextField(mc.fontRenderer, this.left + 1, this.top + 1, bg_width / 2, 20);
    }

    @Override
    public void updateScreen()
    {
        this.search_field.updateCursorCounter();
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
