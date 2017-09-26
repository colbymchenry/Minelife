package com.minelife.minebay.client.gui;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.packet.PacketListings;
import com.minelife.util.NumberConversions;
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

    private Listings listings_gui;
    private GuiLoadingAnimation loadingAnimation;
    private List<ItemListing> listings;
    private static GuiTextField search_field;
    private GuiButton sell_btn;
    private static GuiDropDown dropdown_sort, dropdown_page;
    private int sort_x, sort_y, sort_width, sort_height;
    private static boolean ascend = true;
    private int pages;

    public ListingsGui()
    {
        Minelife.NETWORK.sendToServer(new PacketListings(0, " ", 0, ascend));
        if(dropdown_page != null) dropdown_page.selected = 0;
        if(dropdown_sort != null) dropdown_sort.selected = 0;
        if(search_field != null) search_field.setText("");
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
        search_field.drawTextBox();
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
            dropdown_sort.draw(mc, mouse_x, mouse_y);
            dropdown_page.draw(mc, mouse_x, mouse_y);
            return;
        }

        this.listings_gui.draw(mouse_x, mouse_y, Mouse.getDWheel());
        dropdown_sort.draw(mc, mouse_x, mouse_y);
        dropdown_page.draw(mc, mouse_x, mouse_y);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);

        if (listings_gui != null)
            this.listings_gui.keyTyped(c, i);

        if (search_field != null) {
            if (search_field.isFocused() && i == Keyboard.KEY_RETURN) {
                dropdown_sort.selected = 0;
                dropdown_page.selected = 0;
                Minelife.NETWORK.sendToServer(new PacketListings(NumberConversions.toInt(dropdown_page.options[dropdown_page.selected]) - 1, search_field.getText(), dropdown_sort.selected, ascend));
            }
            search_field.textboxKeyTyped(c, i);
        }
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        if (dropdown_sort != null) {
            int selected = dropdown_sort.selected;
            if (dropdown_sort.mouseClicked(mc, mouse_x, mouse_y)) {
                if (selected != dropdown_sort.selected) {
                    Minelife.NETWORK.sendToServer(new PacketListings(NumberConversions.toInt(dropdown_page.options[dropdown_page.selected]) - 1, search_field.getText(), dropdown_sort.selected, ascend));
                }
                return;
            }
        }

        if (dropdown_page != null) {
            int selected = dropdown_page.selected;
            if (dropdown_page.mouseClicked(mc, mouse_x, mouse_y)) {
                if (selected != dropdown_page.selected) {
                    Minelife.NETWORK.sendToServer(new PacketListings(NumberConversions.toInt(dropdown_page.options[dropdown_page.selected]) - 1, search_field.getText(), dropdown_sort.selected, ascend));
                }
                return;
            }
        }

        if (search_field != null)
            search_field.mouseClicked(mouse_x, mouse_y, mouse_btn);

        if (this.sell_btn != null)
            if (this.sell_btn.mousePressed(mc, mouse_x, mouse_y))
                Minecraft.getMinecraft().displayGuiScreen(new SellItemGui());

        if (mouse_x >= left + search_field.getWidth() + 13 && mouse_x <= left + search_field.getWidth() + 13 + 16) {
            if (mouse_y >= search_field.yPosition + 3 && mouse_y <= search_field.yPosition + 3 + 16) {
                dropdown_page.selected = 0;
                Minelife.NETWORK.sendToServer(new PacketListings(NumberConversions.toInt(dropdown_page.options[dropdown_page.selected]) - 1, search_field.getText(), dropdown_sort.selected, ascend));
            }
        }

        if (mouse_x >= sort_x && mouse_x <= sort_x + sort_width && mouse_y >= sort_y && mouse_y <= sort_y + sort_height) {
            ascend = !ascend;
            Minelife.NETWORK.sendToServer(new PacketListings(NumberConversions.toInt(dropdown_page.options[dropdown_page.selected]) - 1, search_field.getText(), dropdown_sort.selected, ascend));
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

        this.listings_gui = new Listings(mc, this.left, this.top + 26, this.bg_width, this.bg_height - 30, listings, this);
        if (search_field == null)
            search_field = new GuiTextField(mc.fontRenderer, this.left + 1, this.top + 1, (bg_width / 2) - 27, 20);
        else {
            search_field.xPosition = this.left + 1;
            search_field.yPosition = this.top + 1;
            search_field.width = (bg_width / 2) - 27;
        }

        this.sell_btn = new CustomButton(0, this.left + this.bg_width - 35, this.top + 1, "Sell", fontRendererObj);

        if (dropdown_sort == null)
            dropdown_sort = new GuiDropDown(search_field.xPosition + search_field.width + 30, search_field.yPosition + 3, 60, 13, PacketListings.options);
        else {
            dropdown_sort.xPosition = search_field.xPosition + search_field.width + 30;
            dropdown_sort.yPosition = search_field.yPosition + 3;
        }
        dropdown_sort.color_bg = new Color(206, 0, 204);
        dropdown_sort.color_highlight = dropdown_sort.color_bg.darker().darker();

        this.sort_x = dropdown_sort.xPosition + dropdown_sort.width + 5;
        this.sort_y = dropdown_sort.yPosition - 3;
        this.sort_width = 20;
        this.sort_height = 20;

        List<String> page_list = Lists.newArrayList();
        for (int i = 1; i < pages + 1; i++) page_list.add("" + i);

        if (pages == 0) page_list.add("1");

        if (dropdown_page == null)
            dropdown_page = new GuiDropDown(this.sort_x + this.sort_width + 6, dropdown_sort.yPosition, 20, 13, page_list.toArray(new String[page_list.size()]));
        else {
            dropdown_page.xPosition = this.sort_x + this.sort_width + 6;
            dropdown_page.yPosition = dropdown_sort.yPosition;
            dropdown_page.options = page_list.toArray(new String[page_list.size()]);
        }
        dropdown_page.color_bg = new Color(206, 0, 204);
        dropdown_page.color_highlight = dropdown_page.color_bg.darker().darker();
    }

    @Override
    public void updateScreen()
    {
        if (search_field != null) search_field.updateCursorCounter();
    }

    class Listings extends GuiScrollableContent {

        private final List<ItemListing> item_listings;
        private final GuiScreen parent;

        public Listings(Minecraft mc, int xPosition, int yPosition, int width, int height, List<ItemListing> item_listings, GuiScreen parent)
        {
            super(mc, xPosition, yPosition, width, height);
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
