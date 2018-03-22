package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.network.PacketGetItemListings;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiLoadingAnimation;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GuiItemListings extends GuiMinebay {

    private static ResourceLocation texSearch = new ResourceLocation(Minelife.MOD_ID, "textures/gui/magnify.png");
    private static ResourceLocation texSort = new ResourceLocation(Minelife.MOD_ID, "textures/gui/arrow.png");
    private GuiScrollableListing guiListings;
    private GuiLoadingAnimation guiLoading;
    private GuiDropDown guiSort, guiPage;
    private GuiTextField searchField;
    private boolean ascend = true;
    private List<ItemListing> itemListings;
    private int pageCount = 0;

    public GuiItemListings() {
        Minelife.getNetwork().sendToServer(new PacketGetItemListings(0, " ", 0, ascend));
        if (guiPage != null) guiPage.selected = 0;
        if (guiSort != null) guiSort.selected = 0;
        if (searchField != null) searchField.setText("");
    }

    public GuiItemListings(List<ItemListing> itemListings, int pageCount) {
        this.itemListings = itemListings;
        this.pageCount = pageCount;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.itemListings == null) {
            this.guiLoading.drawLoadingAnimation();
            return;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        super.initGui();

        if (this.itemListings == null) {
            this.guiLoading = new GuiLoadingAnimation((this.width - 75) / 2, 20 + (this.height - 75) / 2, 75, 75, Color.white);
            return;
        }

        this.guiListings = new GuiScrollableListing(mc, this.guiLeft, this.guiTop + 26, this.xSize, this.ySize - 30, itemListings);
        this.searchField = new GuiTextField(0, mc.fontRenderer, this.guiLeft + 1, this.guiTop + 1, (xSize / 2) - 27, 20);

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
    public void updateScreen() {
        if (this.searchField != null) this.searchField.updateCursorCounter();
    }

}
