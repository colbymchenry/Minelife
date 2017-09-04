package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.Color;
import java.util.List;

public class GuiPopup extends GuiScreen {

    private String message;
    private int color, width = 100, height, xPosition, yPosition;
    private GuiScreen previousScreen;
    private Color bgColor;
    private List<String> formattedList;

    public GuiPopup(String message, int color, GuiScreen previousScreen) {
        this.message = message;
        this.color = color;
        this.width = width;
        this.height = height;
        this.previousScreen = previousScreen;
        this.bgColor = new Color(color);
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, width, height, bgColor);
        fontRendererObj.setUnicodeFlag(true);
        int lineY = yPosition - 3;
        for (String line : formattedList) {
            drawCenteredString(fontRendererObj, line, xPosition + (width / 2), lineY += fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
        }
        fontRendererObj.setUnicodeFlag(false);
        super.drawScreen(x, y, f);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Minecraft.getMinecraft().displayGuiScreen(previousScreen);
    }

    @Override
    public void initGui() {
        fontRendererObj.setUnicodeFlag(true);
        formattedList = fontRendererObj.listFormattedStringToWidth(message, width);
        height = formattedList.size() * fontRendererObj.FONT_HEIGHT;
        height += 35;
        width = 100;
        width += 5;
        fontRendererObj.setUnicodeFlag(false);
        xPosition = (super.width - width) / 2;
        yPosition = (super.height - height) / 2;
        buttonList.clear();
        buttonList.add(new GuiButton(0, xPosition + ((width - 30) / 2), yPosition + height - 25, 30, 20, "Ok"));
    }
}
