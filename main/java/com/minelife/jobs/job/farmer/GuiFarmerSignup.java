package com.minelife.jobs.job.farmer;

import com.google.common.collect.Lists;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;

import java.util.LinkedList;

public class GuiFarmerSignup extends GuiScreen {

    private int xPos;
    private int bgWidth = 250, bgHeight = 40;

    private int part = 0;
    private LinkedList<String> parts = Lists.newLinkedList();

    public GuiFarmerSignup() {
        parts.add("Hello, " + mc.thePlayer.getDisplayName() + "!");
        parts.add("Thinking of becoming an ole farmer like me eh?");
        parts.add("Well being a farmer isn't as easy as it looks.");
        parts.add("We work day in and day out to provide food and crops for the city.");
        parts.add("Scraping up our hands in the hot sun and planting seeds back in the rich soil.");
        parts.add("Being a farmer doesn't pay much at the beginning");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        String s = parts.get(part);
        int stringHeight = fontRendererObj.listFormattedStringToWidth(s, bgWidth).size() * fontRendererObj.FONT_HEIGHT;
        GuiUtil.drawDefaultBackground(xPos, height - stringHeight - 10, bgWidth, bgHeight);
        fontRendererObj.drawSplitString(s, xPos + 5, height - stringHeight - 5, bgWidth - 10, 4210752);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        super.mouseClicked(mouseX, mouseY, mouseBtn);
    }

    @Override
    public void initGui() {
        super.initGui();
        xPos = (this.width - bgWidth) / 2;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

}