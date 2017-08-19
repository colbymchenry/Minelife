package com.minelife.minebay.client.gui;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.packet.PacketListings;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class ListingsGui extends MasterGui {

    private static ResourceLocation magnify_texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/magnify.png");
    private static final ResourceLocation arrow_texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/arrow.png");

    private static int dropdown_value = 0;

    private Listings listings_gui;
    private GuiLoadingAnimation loadingAnimation;
    private List<ItemListing> listings;
    private GuiTextField search_field;
    private GuiButton sell_btn;
    private GuiDropDown dropdown_sort, dropdown_page;
    private int sort_x, sort_y, sort_width, sort_height;
    private static boolean ascend = true;
    private int pages;
    private static int page = 0;

    public ListingsGui()
    {
        page = 0;
        Minelife.NETWORK.sendToServer(new PacketListings(0, " ", 0, ascend));
    }

    public ListingsGui(List<ItemListing> listings, int pages)
    {
        this.listings = listings;
        this.pages = pages;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        super.drawScreen(mouse_x, mouse_y, f);

        if (listings == null) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        Color color = new Color(72, 0, 70, 200);
        this.drawGradientRect(left - 2, top - 2, left + bg_width, top + 25, color.hashCode(), color.hashCode());
        this.search_field.drawTextBox();
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(magnify_texture);
        GuiUtil.drawImage(left + search_field.getWidth() + 13, search_field.yPosition + 3, 16, 16);
        this.sell_btn.drawButton(mc, mouse_x, mouse_y);


        draw_sort_button:
        {
            boolean hovering = mouse_x >= sort_x && mouse_x <= sort_x + sort_width && mouse_y >= sort_y && mouse_y <= sort_y + sort_height;
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GuiUtil.drawDefaultBackground(sort_x, sort_y, sort_width, sort_height, hovering ? new Color(230, 0, 228) : new Color(203, 0, 203));

            mc.getTextureManager().bindTexture(arrow_texture);

            GL11.glPushMatrix();
            {
                int scale = 2;
                GL11.glTranslatef(sort_x + (!ascend ? 1.5f : 3f), sort_y + 2.5f, 0);
                GL11.glTranslatef(4 * scale, 4 * scale, 4 * scale);
                GL11.glRotatef(ascend ? 180 : 0, 0, 0, 1);
                GL11.glTranslatef(-4 * scale, -4 * scale, -4 * scale);
                Color hover_color = new Color(16777120);
                GL11.glColor4f(hovering ? hover_color.getRed() / 255f : 1, hovering ? hover_color.getGreen() / 255f : 1, hovering ? hover_color.getBlue() / 255f : 1, 1);
                GuiUtil.drawImage(0, 0, 8 * scale, 8 * scale);
            }
            GL11.glPopMatrix();
        }

        GL11.glColor4f(1, 1, 1, 1);

        if (this.listings.isEmpty()) {
            String msg = EnumChatFormatting.BOLD + "Uh-Oh! No items were found :'(";
            int msg_width = mc.fontRenderer.getStringWidth(msg);
            mc.fontRenderer.drawStringWithShadow(msg, (this.width - msg_width) / 2, top + ((bg_height - mc.fontRenderer.FONT_HEIGHT) / 2), 0xFFFFFF);
            this.sell_btn.drawButton(mc, mouse_x, mouse_y);
            this.dropdown_sort.draw(mc, mouse_x, mouse_y);
            this.dropdown_page.draw(mc, mouse_x, mouse_y);
            return;
        }

        this.listings_gui.draw(mouse_x, mouse_y, Mouse.getDWheel());
        this.dropdown_sort.draw(mc, mouse_x, mouse_y);
        this.dropdown_page.draw(mc, mouse_x, mouse_y);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);

        if (listings_gui != null)
            this.listings_gui.keyTyped(c, i);

        if (this.search_field != null) {
            if (this.search_field.isFocused() && i == Keyboard.KEY_RETURN) {
                page = 0;
                Minelife.NETWORK.sendToServer(new PacketListings(page, search_field.getText(), this.dropdown_sort.selected, ascend));
            }
            this.search_field.textboxKeyTyped(c, i);
        }
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        if (this.dropdown_sort != null) {
            int selected = this.dropdown_sort.selected;
            if (this.dropdown_sort.mouseClicked(mc, mouse_x, mouse_y)) {
                if (selected != this.dropdown_sort.selected) {
                    Minelife.NETWORK.sendToServer(new PacketListings(page, search_field.getText(), this.dropdown_sort.selected, ascend));
                    dropdown_value = dropdown_sort.selected;
                }
                return;
            }
        }

        if (this.dropdown_page != null) {
            int selected = this.dropdown_page.selected;
            if (this.dropdown_page.mouseClicked(mc, mouse_x, mouse_y)) {
                if (selected != this.dropdown_page.selected) {
                    page = dropdown_page.selected;
                    Minelife.NETWORK.sendToServer(new PacketListings(page, search_field.getText(), this.dropdown_sort.selected, ascend));
                    dropdown_value = dropdown_page.selected;
                }
                return;
            }
        }

        if (this.search_field != null)
            this.search_field.mouseClicked(mouse_x, mouse_y, mouse_btn);

        if (this.sell_btn != null)
            if (this.sell_btn.mousePressed(mc, mouse_x, mouse_y))
                Minecraft.getMinecraft().displayGuiScreen(new SellItemGui());

        if (mouse_x >= left + search_field.getWidth() + 13 && mouse_x <= left + search_field.getWidth() + 13 + 16) {
            if (mouse_y >= search_field.yPosition + 3 && mouse_y <= search_field.yPosition + 3 + 16) {
                page = 0;
                Minelife.NETWORK.sendToServer(new PacketListings(page, search_field.getText(), this.dropdown_sort.selected, ascend));
            }
        }

        if(mouse_x >= sort_x && mouse_x <= sort_x + sort_width && mouse_y >= sort_y && mouse_y <= sort_y + sort_height) {
            ascend = !ascend;
            Minelife.NETWORK.sendToServer(new PacketListings(page, search_field.getText(), this.dropdown_sort.selected, ascend));
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if (listings == null) {
            loadingAnimation = new GuiLoadingAnimation((this.width - 75) / 2, 20 + (this.height - 75) / 2, 75, 75, Color.white);
            return;
        }

        this.listings_gui = new Listings(this.left, this.top + 26, this.bg_width, this.bg_height - 30, listings, this);
        this.search_field = new GuiTextField(mc.fontRenderer, this.left + 1, this.top + 1, (bg_width / 2) - 27, 20);
        this.sell_btn = new CustomButton(0, this.left + this.bg_width - 35, this.top + 1, "Sell", fontRendererObj);

        this.dropdown_sort = new GuiDropDown(this.search_field.xPosition + this.search_field.width + 30, this.search_field.yPosition + 3, 60, 13, PacketListings.options);
        this.dropdown_sort.color_bg = new Color(206, 0, 204);
        this.dropdown_sort.color_highlight = this.dropdown_sort.color_bg.darker().darker();
        this.dropdown_sort.selected = dropdown_value;

        this.sort_x = this.dropdown_sort.xPosition + this.dropdown_sort.width + 5;
        this.sort_y = this.dropdown_sort.yPosition - 3;
        this.sort_width = 20;
        this.sort_height = 20;

        List<String> page_list = Lists.newArrayList();
// TODO: Fix error when changing page to 2, error on desktop
        for(int i = 1; i < pages + 1; i++) page_list.add("" + i);

        this.dropdown_page = new GuiDropDown(this.sort_x + this.sort_width + 6, this.dropdown_sort.yPosition, 20, 13, page_list.toArray(new String[page_list.size()]));
        this.dropdown_page.color_bg = new Color(206, 0, 204);
        this.dropdown_page.color_highlight = this.dropdown_page.color_bg.darker().darker();
        this.dropdown_page.selected = dropdown_value;
    }

    @Override
    public void updateScreen()
    {
        if (this.search_field != null) this.search_field.updateCursorCounter();
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
            if (doubleClick) {
                Minecraft.getMinecraft().displayGuiScreen(new BuyItemGui(item_listings.get(index)));
            } else {
                item_listings.get(index).mouse_clicked(mouseX, mouseY, false);
            }
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
