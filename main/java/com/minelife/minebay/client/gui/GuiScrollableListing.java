package com.minelife.minebay.client.gui;

import com.minelife.minebay.ItemListing;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GuiScrollableListing extends GuiScrollableContent {

    private ItemStack hoveringStack;
    private List<ItemListing> itemListings;

    public GuiScrollableListing(Minecraft mc, int x, int y, int width, int height, List<ItemListing> itemListings) {
        super(mc, x, y, width, height);
        this.itemListings = itemListings;
    }

    @Override
    public int getObjectHeight(int index) {
        return this.itemListings.get(index).getHeight();
    }

    boolean foundOne = false;

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        if(selected == index) {
            GuiHelper.drawDefaultBackground(0, 0, width - 8, getObjectHeight(index), 0xba00b8);
        }
        this.itemListings.get(index).draw(mouseX, mouseY);
        if(index == 0) foundOne = false;
        if(mouseX >= 3 && mouseX <= 3 + 20 && mouseY >= 3 && mouseY <= 3 + 20 && !foundOne){
            foundOne = true;
            hoveringStack = this.itemListings.get(index).getItemStack();
        }

        if(!foundOne) hoveringStack = null;
    }

    @Override
    public int getSize() {
        return this.itemListings.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
        if (doubleClick) {
            if(itemListings.get(index).getSellerID().equals(mc.player.getUniqueID())) {
                mc.displayGuiScreen(new GuiDeleteListing(itemListings.get(index)));
            } else {
                mc.displayGuiScreen(new GuiBuyItem(itemListings.get(index)));
            }
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

    }

    public ItemStack getHoveringStack() {
        return hoveringStack;
    }
}
