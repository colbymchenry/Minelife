package com.minelife.jobs.job.farmer.gui;

import com.google.common.collect.Lists;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.LinkedList;

public class GuiFarmerSignup extends GuiScreen {

    private int xPos;
    private int bgWidth = 250, bgHeight = 40;

    private int part = 0;
    private LinkedList<String> parts = Lists.newLinkedList();

    public GuiFarmerSignup() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        String s = parts.get(part);
        int stringHeight = fontRenderer.listFormattedStringToWidth(s, bgWidth - 5).size() * fontRenderer.FONT_HEIGHT;
        GuiHelper.drawDefaultBackground(xPos, height - stringHeight - 40, bgWidth, stringHeight + fontRenderer.FONT_HEIGHT);
        fontRenderer.drawSplitString(s, xPos + 5, height - stringHeight - 35, bgWidth - 5, 4210752);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        super.keyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseBtn);
        part++;
        if(part >= parts.size()) part = parts.size() - 1;
    }

    @Override
    public void initGui() {
        super.initGui();
        xPos = (this.width - bgWidth) / 2;
        if(parts.isEmpty()) {
            parts.add("Hello, " + mc.player.getName() + "!");
            parts.add("Thinking of becoming an ole farmer like me eh?");
            parts.add("Well being a farmer isn't as easy as it looks.");
            parts.add("We work day in and day out to provide food and crops for the city.");
            parts.add("Scraping up our hands in the hot sun and planting seeds back in the rich soil.");
            parts.add("Being a farmer doesn't pay much at the beginning");
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

}